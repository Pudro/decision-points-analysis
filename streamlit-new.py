import streamlit as st
import pm4py
from pm4py.objects.log.importer.xes import importer as xes_importer
from pm4py.objects.petri_net.importer import importer as pnml_importer
from pm4py.visualization.petri_net import visualizer as pn_visualizer
import os
import copy
import json
import numpy as np
import pandas as pd
from utils import shorten_rules_manually, discover_overlapping_rules, sampling_dataset
from utils import get_attributes_from_event
from utils import get_map_transitions_events, get_decision_points_and_targets
from utils import get_all_dp_from_event_to_sink
from daikon_utils import discover_branching_conditions
from DecisionTree import DecisionTree
from sklearn import metrics
from pm4py.algo.filtering.log.variants import variants_filter
from tqdm import tqdm
import datetime

@st.experimental_memo
def analyse_ds(_log):
    unique_values_trace = dict()
    unique_values = dict()
    for trace in log:
        for trace_attr in trace.attributes:
            if trace_attr in unique_values_trace.keys():
                unique_values_trace[trace_attr].add(trace.attributes[trace_attr])
            else:
                unique_values_trace[trace_attr] = {trace.attributes[trace_attr]}
        for event in trace:
            for ev_attr in event:
                if ev_attr in unique_values.keys():
                    unique_values[ev_attr].add(event[ev_attr])
                else:
                    unique_values[ev_attr] = {event[ev_attr]}
    max_len = 0
    max_val = None
    for value in unique_values.keys():
        if len(unique_values[value]) > max_len:
            max_len = len(unique_values[value])
            max_val = value
    for value in unique_values.keys():
        old_values = list(copy.copy(unique_values[value]))
        old_values.extend([np.nan]*(max_len - len(unique_values[value])))
        unique_values[value] = old_values
    for value in unique_values_trace.keys():
        old_values = list(copy.copy(unique_values_trace[value]))
        old_values.extend([np.nan]*(max_len - len(unique_values_trace[value])))
        unique_values_trace[value] = old_values
    df_trace = pd.DataFrame.from_dict(unique_values_trace)
    df = pd.DataFrame.from_dict(unique_values)

    return df_trace, df, unique_values, unique_values_trace

def save_json(dict_conf, data_dir='dt-attributes'):
    json_string = json.dumps(dict_conf)
    file_name = '{}.attr'.format(st.session_state.uploaded_log_name)
    with open(os.path.join(data_dir, file_name), 'w') as file:
        json.dump(json_string, file)

def create_dict(st_session_state):
    dict_conf = dict()
    for name in st_session_state:
        if ('e_' in name or 't_' in name) and name.split('_')[1] not in ['trace', 'event']:
            # remove initial 'e_' or 't_' from the name
            dict_conf["_".join(name.split('_')[1:])] = st.session_state[name]
    return dict_conf

########## BEGINNING ##########

if 'uploaded_log_name' not in st.session_state:
    st.session_state['uploaded_log_name'] = None
    st.session_state['attr_config_saved'] = False
    st.session_state['results_selection'] = False

uploaded_event_log = st.file_uploader('Choose the event log file in .xes format')

if uploaded_event_log is not None:
    if st.session_state.uploaded_log_name != uploaded_event_log.name:
        if not os.path.exists('streamlitTemp'):
            os.makedirs('streamlitTemp')
        with open(os.path.join('streamlitTemp', uploaded_event_log.name), 'wb') as f:
            f.write(uploaded_event_log.getbuffer())

        log = xes_importer.apply('streamlitTemp/{}'.format(uploaded_event_log.name))

        with st.spinner("Analysing dataset, don't worry be happy!"):
            df_trace, df, unique_values, unique_values_trace = analyse_ds(log)
            st.write('First 10 unique values of trace attributes: ', df_trace.head(10),
                     'First 10 unique values of events attributes: ', df.head(10))
        col_event, col_trace = st.columns(2)
        with col_trace:
            list_trace_attr = list()
            st.header("Trace attributes")
            for i, value in enumerate(unique_values_trace.keys()):
                st.selectbox(value, ('continuous', 'categorical', 'boolean'), key='t_{}'.format(value))
                list_trace_attr.append('t_{}'.format(value))
        with col_event:
            list_event_attr = list()
            st.header("Event attributes")
            for i, value in enumerate(unique_values.keys()):
                if value != 'time:timestamp':
                    st.selectbox(value, ('continuous', 'categorical', 'boolean'), key='e_{}'.format(value))
                    list_event_attr.append('e_{}'.format(value))
        st.session_state.df = df
        st.session_state.df_trace = df_trace
        st.session_state.list_trace_attr = list_trace_attr
        st.session_state.list_event_attr = list_event_attr
        st.session_state.uploaded_log_name = uploaded_event_log.name.split('log-')[1].split('.xes')[0]
        st.session_state.log = log
    else:
        st.write('First 10 unique values of trace attributes: ', st.session_state.df_trace.head(10),
                 'First 10 unique values of events attributes: ', st.session_state.df.head(10))
        col_event, col_trace = st.columns(2)
        with col_trace:
            st.header("Trace attributes")
            for name in st.session_state.list_trace_attr:
                if 't_' in name:
                    st.selectbox(name.split('t_')[1], ('continuous', 'categorical', 'boolean'), key=name)
        with col_event:
            st.header("Event attributes")
            for name in st.session_state.list_event_attr:
                st.selectbox(name.split('e_')[1], ('continuous', 'categorical', 'boolean'), key=name)

    if st.button('Save Configuration'):
        dict_conf = create_dict(st.session_state)
        save_json(dict_conf)
        st.session_state.attr_config_saved = True

    if st.session_state.attr_config_saved:
        # Importing the attributes_map file
        attributes_map_file = '{}.attr'.format(st.session_state.uploaded_log_name)
        with open(os.path.join('dt-attributes', attributes_map_file), 'r') as f:
            json_string = json.load(f)
            attributes_map = json.loads(json_string)

        # Converting attributes types according to the attributes_map file
        for trace in st.session_state.log:
            for event in trace:
                for attribute in event.keys():
                    if attribute in attributes_map:
                        if attributes_map[attribute] == 'continuous':
                            event[attribute] = float(event[attribute])
                        elif attributes_map[attribute] == 'boolean':
                            event[attribute] = bool(event[attribute])

        # Importing the Petri net model, if it exists
        if 'net' not in st.session_state:
            try:
                st.session_state.net, st.session_state.im, st.session_state.fm = pnml_importer.apply("models/{}.pnml".format(st.session_state.uploaded_log_name))
            except FileNotFoundError:
                print("Existing Petri Net model not found. Extracting one using the Inductive Miner...")
                st.session_state.net, st.session_state.im, st.session_state.fm = pm4py.discover_petri_net_inductive(st.session_state.log)

        gviz = pn_visualizer.apply(st.session_state.net, st.session_state.im, st.session_state.fm)
        gviz.graph_attr['bgcolor'] = 'white'
        st.graphviz_chart(gviz)

        sink_complete_net = [place for place in st.session_state.net.places if place.name == 'sink'][0]
        trans_events_map = get_map_transitions_events(st.session_state.net)

        # Scanning the log to get the data related to decision points
        print('Extracting training data from Event Log...')
        decision_points_data, event_attr, stored_dicts = dict(), dict(), dict()
        variants = variants_filter.get_variants(st.session_state.log)
        # Decision points of interest are searched considering the variants only
        for variant in tqdm(variants):
            transitions_sequence, events_sequence = list(), list()
            dp_events_sequence = dict()
            for i, event_name in enumerate(variant.split(',')):
                trans_from_event = trans_events_map[event_name]
                transitions_sequence.append(trans_from_event)
                events_sequence.append(event_name)
                if len(transitions_sequence) > 1:
                    dp_dict, stored_dicts = get_decision_points_and_targets(transitions_sequence, st.session_state.net, stored_dicts)
                    dp_events_sequence['Event_{}'.format(i + 1)] = dp_dict

            # Final update of the current trace (from last event to sink)
            transition = [trans for trans in st.session_state.net.transitions if trans.label == event_name][0]
            dp_events_sequence['End'] = get_all_dp_from_event_to_sink(transition, sink_complete_net, dp_events_sequence)

            for trace in variants[variant]:
                # Storing the trace attributes (if any)
                if len(trace.attributes.keys()) > 1 and 'concept:name' in trace.attributes.keys():
                    event_attr.update(trace.attributes)

                # Keeping the same attributes observed in previously (to keep dictionaries at the same length)
                event_attr = {k: np.nan for k in event_attr.keys()}

                transitions_sequence = list()
                for i, event in enumerate(trace):
                    trans_from_event = trans_events_map[event["concept:name"]]
                    transitions_sequence.append(trans_from_event)

                    # Appending the last attribute values to the decision point dictionary
                    if len(transitions_sequence) > 1:
                        dp_dict = dp_events_sequence['Event_{}'.format(i + 1)]
                        for dp in dp_dict.keys():
                            # Adding the decision point to the total dictionary if it is not already there
                            if dp not in decision_points_data.keys():
                                decision_points_data[dp] = {k: [] for k in ['target']}
                            for dp_target in dp_dict[dp]:
                                for a in event_attr.keys():
                                    # Attribute not present and not nan: add it as new key and fill previous entries as nan
                                    if a not in decision_points_data[dp] and event_attr[a] is not np.nan:
                                        n_entries = len(decision_points_data[dp]['target'])
                                        decision_points_data[dp][a] = [np.nan] * n_entries
                                        decision_points_data[dp][a].append(event_attr[a])
                                    # Attribute present: just append it to the existing list
                                    elif a in decision_points_data[dp]:
                                        decision_points_data[dp][a].append(event_attr[a])
                                # Appending also the target transition label to the decision point dictionary
                                decision_points_data[dp]['target'].append(dp_target)

                    # Updating the attribute values dictionary with the values from the current event
                    event_attr.update(get_attributes_from_event(event))

                # Appending the last attribute values to the decision point dictionary (from last event to sink)
                if len(dp_events_sequence['End']) > 0:
                    for dp in dp_events_sequence['End'].keys():
                        if dp not in decision_points_data.keys():
                            decision_points_data[dp] = {k: [] for k in ['target']}
                        for dp_target in dp_events_sequence['End'][dp]:
                            for a in event_attr.keys():
                                if a not in decision_points_data[dp] and event_attr[a] is not np.nan:
                                    n_entries = len(decision_points_data[dp]['target'])
                                    decision_points_data[dp][a] = [np.nan] * n_entries
                                    decision_points_data[dp][a].append(event_attr[a])
                                elif a in decision_points_data[dp]:
                                    decision_points_data[dp][a].append(event_attr[a])
                            decision_points_data[dp]['target'].append(dp_target)

        # TODO put if above to avoid computation if already in session state
        st.session_state.decision_points_data = decision_points_data

        if not st.session_state.results_selection:
            st.session_state.method = st.selectbox('Decision Trees or Daikon?', ('Choose one...', 'Decision Trees', 'Daikon'))
            if st.session_state.method == 'Decision Trees':
                st.session_state.pruning = st.selectbox('Pruning method?', ('Choose one...', 'Rules simplification', 'Pessimistic', 'No Pruning'))
                if st.session_state.pruning != 'Choose one...':
                    st.session_state.overlapping = st.selectbox('Overlapping rules?', ('Choose one...', 'Yes', 'No'))
                    if st.session_state.overlapping != 'Choose one...':
                        st.session_state.results_selection = True
                        st.experimental_rerun()
            elif st.session_state.method == 'Daikon':
                st.session_state.method = 'Daikon'
                st.session_state.results_selection = True
                st.experimental_rerun()

        # Data has been gathered. For each decision point, fitting a decision tree on its data and extracting the rules
        else:
            file_name = '{}_{}.txt'.format(st.session_state.uploaded_log_name, datetime.datetime.now().strftime("%d-%m-%Y_%H-%M-%S"))
            for decision_point in decision_points_data.keys():
                st.write("\nDecision point: {}".format(decision_point))
                dataset = pd.DataFrame.from_dict(decision_points_data[decision_point])
                # Replacing ':' with '_' both in the dataset columns and in the attributes map since ':' creates problems
                dataset.columns = dataset.columns.str.replace(':', '_')
                attributes_map = {k.replace(':', '_'): attributes_map[k] for k in attributes_map}

                # Discovering branching conditions with Daikon
                if st.session_state.method == 'Daikon':
                    st.write("Discovering branching conditions with Daikon...")
                    rules = discover_branching_conditions(dataset)
                    rules = {k: rules[k].replace('_', ':') for k in rules}
                    st.write(rules)
                else:
                    st.write("Fitting a decision tree on the decision point's dataset...")
                    accuracies, f_scores = list(), list()
                    for i in tqdm(range(10)):
                        # Sampling
                        dataset = sampling_dataset(dataset)

                        # Fitting
                        dt = DecisionTree(attributes_map)
                        dt.fit(dataset)

                        # Predict
                        y_pred = dt.predict(dataset.drop(columns=['target']))

                        # Accuracy
                        accuracy = metrics.accuracy_score(dataset['target'], y_pred)
                        accuracies.append(accuracy)

                        # F1-score
                        if len(dataset['target'].unique()) > 2:
                            f1_score = metrics.f1_score(dataset['target'], y_pred, average='weighted')
                        else:
                            f1_score = metrics.f1_score(dataset['target'], y_pred, pos_label=dataset['target'].unique()[0])
                        f_scores.append(f1_score)

                    # Rules extraction
                    if len(dt.get_nodes()) > 1:
                        st.write("Training complete. Extracting rules...")
                        with open(file_name, 'a') as f:
                            f.write('{} - SUCCESS\n'.format(decision_point))
                            f.write('Dataset target values counts:\n {}\n'.format(dataset['target'].value_counts()))

                            st.write("Train accuracy: {}".format(sum(accuracies) / len(accuracies)))
                            f.write('Accuracy: {}\n'.format(sum(accuracies) / len(accuracies)))
                            st.write("F1 score: {}".format(sum(f_scores) / len(f_scores)))
                            f.write('F1 score: {}\n'.format(sum(f_scores) / len(f_scores)))

                            if st.session_state.pruning == 'No Pruning':
                                # Rule extraction without pruning
                                rules = dt.extract_rules()
                            elif st.session_state.pruning == 'Pessimistic':
                                # Alternative pruning (directly on tree)
                                dt.pessimistic_pruning(dataset)
                                rules = dt.extract_rules()
                            elif st.session_state.pruning == 'Rules simplification':
                                # Rule extraction with pruning
                                rules = dt.extract_rules_with_pruning(dataset)

                            if st.session_state.overlapping == 'Yes':
                                # Overlapping rules discovery
                                rules = discover_overlapping_rules(dt, dataset, attributes_map, rules)

                            rules = shorten_rules_manually(rules, attributes_map)
                            rules = {k: rules[k].replace('_', ':') for k in rules}

                            f.write('Rules:\n')
                            for k in rules:
                                f.write('{}: {}\n'.format(k, rules[k]))
                            f.write('\n')
                        st.write(rules)
                    else:
                        with open(file_name, 'a') as f:
                            f.write('{} - FAIL\n'.format(decision_point))
                            f.write('Dataset target values counts: {}\n'.format(dataset['target'].value_counts()))

    import shutil
    shutil.rmtree('./streamlitTemp')