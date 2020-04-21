package com.wfb.analysis;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import pipe.reachability.algorithm.*;
import pipe.reachability.algorithm.parallel.MassiveParallelStateSpaceExplorer;
import pipe.reachability.algorithm.sequential.SequentialStateSpaceExplorer;
import uk.ac.imperial.io.*;
import uk.ac.imperial.pipe.exceptions.InvalidRateException;
import uk.ac.imperial.pipe.io.PetriNetIOImpl;
import uk.ac.imperial.pipe.io.PetriNetReader;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;
import uk.ac.imperial.state.ClassifiedState;
import uk.ac.imperial.state.Record;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StateSpaceLoader {
    private static final Logger LOGGER = Logger.getLogger(StateSpaceLoader.class.getName());
    private static final int STATES_PER_THREAD = 100;
    private PetriNet petriNet;
    private Path temporaryTransitions;
    private Path temporaryStates;

    public StateSpaceLoader(File file) {
        loadData(file);
    }

    private void loadData(File file) {
        try {
            PetriNetReader petriNetIO = new PetriNetIOImpl();
            this.petriNet = petriNetIO.read(file.getAbsolutePath());
        } catch (JAXBException | FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }

    /**
     * Calculates the steady state exploration of a Petri net and stores its results
     * in a temporary file.
     * <p>
     * These results are then read in and turned into a graphical representation using mxGraph
     * which is displayed to the user
     * </p>
     * @param threads across which to spread work
     * @return state space explorer results
     */
    public StateSpaceExplorer.StateSpaceExplorerResults calculateResult(int threads)
            throws Exception{

        KryoStateIO stateWriter = new KryoStateIO();
        this.temporaryTransitions = Files.createTempFile("transitions", ".tmp");
        this.temporaryStates = Files.createTempFile("states", ".tmp");
        if (petriNet == null) {
            throw new Exception("Error in loaded Petri net, could not read PNML file.");
        }
        ExplorerUtilities explorerUtils = new BoundedExplorerUtilities(petriNet, threads);
        VanishingExplorer vanishingExplorer = new SimpleVanishingExplorer();
        return generateStateSpace(stateWriter, temporaryTransitions, temporaryStates, petriNet, explorerUtils,
                vanishingExplorer, threads);
    }

    /**
     * Writes the state space into transitions and states
     *
     * @param stateWriter writer
     * @param transitions to write
     * @param states to write
     * @param threads number of worker threads to use
     * @return state space explorer results
     */
    private StateSpaceExplorer.StateSpaceExplorerResults generateStateSpace(StateWriter stateWriter, Path transitions,
                                                                            Path states, PetriNet petriNet,
                                                                            ExplorerUtilities explorerUtils,
                                                                            VanishingExplorer vanishingExplorer,
                                                                            int threads)
            throws Exception {
        try (OutputStream transitionStream = Files.newOutputStream(transitions);
             OutputStream stateStream = Files.newOutputStream(states)) {
            try (Output transitionOutput = new Output(transitionStream);
                 Output stateOutput = new Output(stateStream)) {
                return writeStateSpace(stateWriter, transitionOutput, stateOutput, petriNet, explorerUtils,
                        vanishingExplorer, threads);
            }
        }
    }

    /**
     * Writes the petriNet state space out to a temporary file which is referenced by the objectOutputStream
     *
     * @param stateWriter       format in which to write the results to
     * @param transitionOutput  stream to write state space to
     * @param stateOutput       stream to write state integer mappings to
     * @param explorerUtilites  explorer utilities
     * @param threads number of worker threads to use
     * @param vanishingExplorer
     * @return state space explorer results
     */
    private StateSpaceExplorer.StateSpaceExplorerResults writeStateSpace(StateWriter stateWriter,
                                                                         Output transitionOutput, Output stateOutput,
                                                                         PetriNet petriNet,
                                                                         ExplorerUtilities explorerUtilites,
                                                                         VanishingExplorer vanishingExplorer, int threads)
            throws TimelessTrapException, ExecutionException, InterruptedException, IOException, InvalidRateException {
        StateProcessor processor = new StateIOProcessor(stateWriter, transitionOutput, stateOutput);
        StateSpaceExplorer stateSpaceExplorer = getStateSpaceExplorer(explorerUtilites, vanishingExplorer, processor, threads);
        return stateSpaceExplorer.generate(explorerUtilites.getCurrentState());
    }

    private StateSpaceExplorer getStateSpaceExplorer( ExplorerUtilities explorerUtilites, VanishingExplorer vanishingExplorer, StateProcessor stateProcessor, int threads) {
        if (threads == 1) {
            return new SequentialStateSpaceExplorer(explorerUtilites, vanishingExplorer, stateProcessor);
        }
        return new MassiveParallelStateSpaceExplorer(explorerUtilites, vanishingExplorer, stateProcessor, threads, STATES_PER_THREAD);
    }

    /**
     * Reads results of steady state exploration into a collection of records
     *
     * @param stateReader reader
     * @param input to process
     * @return state transitions with rates
     * @throws IOException error doing IO
     */
    private Collection<Record> readResults(StateReader stateReader, Input input) throws IOException {
        MultiStateReader reader = new EntireStateReader(stateReader);
        return reader.readRecords(input);
    }

    /**
     * @param records to process
     * @return the number of transitions in the state space
     */
    private int getTransitionCount(Iterable<Record> records) {
        int sum = 0;
        for (Record record : records) {
            sum += record.successors.size();
        }
        return sum;
    }

    /**
     * Loads and processes state space
     *
     * @return results
     * @throws IOException error doing IO
     */
    public Results loadStateSpace() throws IOException {
        KryoStateIO stateReader = new KryoStateIO();
        try (InputStream inputStream = Files.newInputStream(temporaryTransitions);
             InputStream stateInputStream = Files.newInputStream(temporaryStates);
             Input transitionInput = new Input(inputStream);
             Input stateInput = new Input(stateInputStream)) {
            Collection<Record> records = readResults(stateReader, transitionInput);
            Map<Integer, ClassifiedState> stateMap = readMappings(stateReader, stateInput);
            return new Results(records, stateMap);
        }
    }

    /**
     * Reads results of the mapping of an integer state representation to
     * the Classified State it represents
     *
     * @param stateReader reader
     * @param input to process
     * @return state mappings
     * @throws IOException error doing IO
     */
    private Map<Integer, ClassifiedState> readMappings(StateReader stateReader, Input input) throws IOException {
        MultiStateReader reader = new EntireStateReader(stateReader);
        return reader.readStates(input);
    }

    /**
     * State space exploration results
     */
    public class Results {
        /**
         * Transition records
         */
        public final Collection<Record> records;

        /**
         * Classified state mappings
         */
        public final Map<Integer, ClassifiedState> stateMappings;

        /**
         * Constructor
         *
         * @param records of results
         * @param stateMappings state mappings
         */
        public Results(Collection<Record> records, Map<Integer, ClassifiedState> stateMappings) {
            this.records = records;
            this.stateMappings = stateMappings;
        }
    }
}
