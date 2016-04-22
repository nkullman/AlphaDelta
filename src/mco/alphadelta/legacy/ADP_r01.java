import ilog.concert.*;
import ilog.cplex.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ADP_r01 extends JFrame {
    // fields created by WindowBuilder
    private JPanel contentPane;
    private JTextField textTimeLimit;
    private JTextField textIntegralityGap;
    private JTextField textOptimalityGap;
    private JTextField textWorkingMemory;
    private JTextField textNumberOfThreads;
    private JTextField textInputModel;
    private JTextField textOutputDestination;
    private JTextField textAlphaDegrees;
    private JTextField textNumberOfObjectives;

    // fileChooser fields
    private JFileChooser outputDestinationChooser;
    private JFileChooser cpxChooser;

    // fields specific to the AD algorithm
    // Note: future versions of the program will have many of these
    // fields wrapped up into an AD algorithm object
    // Also, swap arrays with ArrayLists
    public static int numObjectives;
    private static String inputFilename;
    private static String outputDestination;
    private static double alphaDegrees;
    private static double alphaRadians;
    private static IloCplex cplex;
    private static IloObjective objFunction;
    private static IloLPMatrix lpMatrix;
    private static int numCols;
    private static ArrayList<String> objectives;
    private static ArrayList<Boolean> objectivesMax;
    private static IloObjectiveSense objSense;
    private static double[] objDeltas;
    private static int cpxNumThreads;
    private static double cpxTimeLimit;
    private static double cpxWorkingMem;
    private static double cpxOptimalityGap;
    private static double cpxIntegralityGap;
    private static int guiCounter = 0;
    private static String timeStamp;


    /**
     * launch the application
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ADP_r01 frame = new ADP_r01();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Constructor for the program which is a subclass of JFrame
     * Note: In future releases, the frame will be a separate, simpler object
     */
    public ADP_r01() {
        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        try {
            cplex = new IloCplex();
            /*
			 * Basic details of the frame
			 */
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setBounds(100, 100, 615, 500);
            contentPane = new JPanel();
            contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
            setTitle("Alpha Delta Algorithm");
            setContentPane(contentPane);
            contentPane.setLayout(new BorderLayout(0, 0));
			
			/*
			 * Create a tabbedPane, add it to the frame 
			 */
            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.setTabPlacement(JTabbedPane.TOP);
            contentPane.add(tabbedPane, BorderLayout.CENTER);
	
			/*
			 * Create panel for obtaining general problem information and model parameters and add it as a tab
			 */
            JPanel panelMain = new JPanel();
            tabbedPane.addTab("General", null, panelMain, null);
            panelMain.setLayout(null);
            // add label to signal input model selection
            JLabel lblInputModel = new JLabel("Input model");
            lblInputModel.setHorizontalAlignment(SwingConstants.RIGHT);
            lblInputModel.setBounds(14, 34, 94, 14);
            lblInputModel.setToolTipText("Use the \"Browse\" button to navigate to the CPLEX model file (.cpx, .lp, .txt) for the model you wish to solve.");
            panelMain.add(lblInputModel);
            // add label and text for the number of objectives
            JLabel lblNumberOfObjectives = new JLabel("Number of objectives");
            lblNumberOfObjectives.setEnabled(false);
            lblNumberOfObjectives.setBounds(45, 118, 122, 14);
            lblNumberOfObjectives.setToolTipText("This value is determined automatically from the objective function of your input model");
            panelMain.add(lblNumberOfObjectives);
            textNumberOfObjectives = new JTextField();
            textNumberOfObjectives.setEnabled(false);
            textNumberOfObjectives.setEditable(false);
            textNumberOfObjectives.setBounds(194, 115, 79, 20);
            textNumberOfObjectives.setToolTipText("This value is determined automatically from the objective function of your input model");
            panelMain.add(textNumberOfObjectives);
            textNumberOfObjectives.setColumns(10);
            // add text field to show the selected file
            textInputModel = new JTextField();
            textInputModel.setBounds(238, 34, 336, 20);
            textInputModel.setEditable(false);
            textInputModel.setToolTipText("Use the \"Browse\" button to navigate to the CPLEX model file (.cpx, .lp, .txt) for the model you wish to solve.");
            panelMain.add(textInputModel);
            textInputModel.setColumns(10);
            // text label for output destination
            JLabel lblOutputDestination = new JLabel("Output destination");
            lblOutputDestination.setHorizontalAlignment(SwingConstants.RIGHT);
            lblOutputDestination.setBounds(16, 62, 92, 14);
            lblOutputDestination.setToolTipText("Use the \"Browse\" button to navigate to the folder where you would like to save model output");
            panelMain.add(lblOutputDestination);
            // add the button to run ADP
            JButton btnRunAlphaDelta = new JButton("Run Alpha-Delta");
            btnRunAlphaDelta.setEnabled(false);
            btnRunAlphaDelta.setBounds(373, 334, 140, 36);
            btnRunAlphaDelta.setToolTipText("Run Alpha-Delta algorithm to generate efficient frontier");
            panelMain.add(btnRunAlphaDelta);
            // add button for selection of output destination
            JButton btnOutputDestination = new JButton("Browse...");
            btnOutputDestination.setBounds(127, 58, 101, 23);
            btnOutputDestination.setToolTipText("Use the \"Browse\" button to navigate to the folder where you would like to save output");
            panelMain.add(btnOutputDestination);
            outputDestinationChooser = new JFileChooser();
            outputDestinationChooser.setDialogTitle("Please select destination for algorithm output");
            outputDestinationChooser.setPreferredSize(new Dimension(600, 500));
            outputDestinationChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            outputDestinationChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            btnOutputDestination.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    outputDestinationChooser.showOpenDialog(null);
                    outputDestination = outputDestinationChooser.getSelectedFile().getAbsolutePath();
                    outputDestination = outputDestination.replace("\\", "\\\\");
                    // make folder to store output
                    File dir = new File(outputDestination + "\\\\ADP_" + timeStamp);
                    dir.mkdir();
                    // set that folder to our output folder
                    outputDestination += "\\\\ADP_" + timeStamp + "\\\\";
                    textOutputDestination.setText(outputDestination);
                    guiCounter++;
                    if (guiCounter == 3) btnRunAlphaDelta.setEnabled(true);
                }
            });
            // add text field to show destination for output files chosen with btnOutputDesination
            textOutputDestination = new JTextField();
            textOutputDestination.setBounds(238, 62, 336, 20);
            textOutputDestination.setEditable(false);
            textOutputDestination.setToolTipText("Use the \"Browse\" button to navigate to the folder where you would like to save output");
            panelMain.add(textOutputDestination);
            textOutputDestination.setColumns(10);
            // label for model parameter alpha
            // In future release: add question mark button that takes user to graphic that helps explain algorithm
            JLabel lblAlphaDegrees = new JLabel("Alpha (degrees)");
            lblAlphaDegrees.setEnabled(false);
            lblAlphaDegrees.setBounds(45, 158, 77, 14);
            lblAlphaDegrees.setToolTipText("Relative weight given to non-primary objectives. Small values ( << 1) recommended for higher precision. ");
            panelMain.add(lblAlphaDegrees);
            // obtain alpha parameter (in degrees) from user
            textAlphaDegrees = new JTextField();
            textAlphaDegrees.setEnabled(false);
            textAlphaDegrees.setBounds(194, 155, 79, 20);
            textAlphaDegrees.setToolTipText("Relative weight given to non-primary objectives. Small values ( << 1) recommended for higher precision. ");
            panelMain.add(textAlphaDegrees);
            textAlphaDegrees.setColumns(10);
            textAlphaDegrees.setText("0.001");
            // label for the increment sizes for the objectives
            JLabel lblObjectiveIncrementSizes = new JLabel("Objective increment sizes");
            lblObjectiveIncrementSizes.setEnabled(false);
            lblObjectiveIncrementSizes.setBounds(45, 199, 122, 14);
            panelMain.add(lblObjectiveIncrementSizes);
            // add button, which opens up new frame to allow user to input objective increments
            // Future release:	add question mark button to take user to visual aid for delta values
            // 					add option to set increment (delta) sizes as a fraction of the objective's ideal
            JButton btnSetIncrementSizes = new JButton("Set increment sizes...");
            btnSetIncrementSizes.setEnabled(false);
            btnSetIncrementSizes.setBounds(194, 195, 155, 23);
            btnSetIncrementSizes.setToolTipText("Size of improvement required for non-primary objectives");
            panelMain.add(btnSetIncrementSizes);
            // add button to allow user to choose input model.
            // Calculate the number of objectives
            JButton btnInputModel = new JButton("Browse...");
            btnInputModel.setBounds(127, 30, 101, 23);
            btnInputModel.setToolTipText("Navigate to the CPLEX model file (.cpx, .lp, .txt) for the model you wish to solve.");
            panelMain.add(btnInputModel);

            JLabel lblObjDeltasStatus = new JLabel("");
            lblObjDeltasStatus.setBounds(373, 199, 201, 14);
            panelMain.add(lblObjDeltasStatus);
            cpxChooser = new JFileChooser();
            cpxChooser.setDialogTitle("Please select an input CPLEX model");
            cpxChooser.setPreferredSize(new Dimension(600, 500));
            cpxChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            btnInputModel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    Supporting2_WaitFrame pleaseWait = new Supporting2_WaitFrame();
                    pleaseWait.setVisible(true);
                    cpxChooser.showOpenDialog(null);
                    File cpxFile = cpxChooser.getSelectedFile();
                    inputFilename = cpxFile.getAbsolutePath();
                    inputFilename = inputFilename.replace("\\", "\\\\"); // Future release: is this step necessary
                    textInputModel.setText(inputFilename);
                    try {
                        cplex.importModel(inputFilename);
                        // Future release:	add error handling for failure to import model
                        System.out.println("Model imported");
                        objFunction = cplex.getObjective();
                        objSense = objFunction.getSense();
                        lpMatrix = (IloLPMatrix) cplex.LPMatrixIterator().next();
                        numCols = cplex.getNcols();

                        // Future Release: Obtain objectives with a token-based scanner (and in a separate method)
                        //First, convert objective function to string
                        String strOrigObjective = objFunction.toString();

                        // Trim the parts of the objective function that are not mathematical
                        int startOfObjExpr = strOrigObjective.indexOf(":");
                        int endOfObjExpr = strOrigObjective.length();
                        String strObjExpr = strOrigObjective.substring(startOfObjExpr + 2, endOfObjExpr);

                        // Divide the remaining string into substrings divided by whitespace
                        String[] objTerms = strObjExpr.split("\\s+");

                        // Some variables we'll need:
                        objectives = new ArrayList<String>(); //holder for the two pieces for each term: coeff and variable
                        objectivesMax = new ArrayList<Boolean>();
                        String[] currentTerm = new String[2]; //first element is coefficient, second element is variable
                        double currentCoeff = 0.0;
                        String currentVar = "";

                        // Cycle through the strings in the objective to find those whose terms that have nonzero coefficients
                        for (int i = 0; i < objTerms.length; i++) {
                            if (i == 0) {
                                currentTerm = objTerms[i].split("\\*");
                                currentCoeff = Double.parseDouble(currentTerm[0]);
                                currentVar = currentTerm[1];

                                if (currentCoeff != 0) {
                                    objectives.add(currentVar);
                                    if (objSense == IloObjectiveSense.Maximize) {
                                        if (currentCoeff < 0) {
                                            objectivesMax.add(false);
                                            System.out.println("Added " + currentVar + " to list of objectives. Goal is to minimize.");
                                        } else if (currentCoeff > 0) {
                                            objectivesMax.add(true);
                                            System.out.println("Added " + currentVar + " to list of objectives. Goal is to maximize.");
                                        }
                                    } else if (objSense == IloObjectiveSense.Minimize) // objective sense is minimize
                                    {
                                        if (currentCoeff < 0) // minimize negative = maximize
                                        {
                                            objectivesMax.add(true);
                                            System.out.println("Added " + currentVar + " to list of objectives. Goal is to maximize.");
                                        } else if (currentCoeff > 0) // minimize positive = minimize
                                        {
                                            objectivesMax.add(false);
                                            System.out.println("Added " + currentVar + " to list of objectives. Goal is to minimize.");
                                        }
                                    }
                                }
                            } else if (i % 2 == 0) {
                                currentTerm = objTerms[i].split("\\*");
                                currentCoeff = Double.parseDouble(currentTerm[0]);
                                currentVar = currentTerm[1];
                                if (currentCoeff != 0) {
                                    objectives.add(currentVar);
                                    if (objSense == IloObjectiveSense.Maximize) {
                                        if (objTerms[i - 1].equals("-")) {
                                            objectivesMax.add(false);
                                            System.out.println("Added " + currentVar + " to list of objectives. Goal is to minimize.");
                                        } else if (objTerms[i - 1].equals("+")) {
                                            objectivesMax.add(true);
                                            System.out.println("Added " + currentVar + " to list of objectives. Goal is to maximize.");
                                        }
                                    } else if (objSense == IloObjectiveSense.Minimize) {
                                        if (objTerms[i - 1].equals("-")) // minimize negative = maximize
                                        {
                                            objectivesMax.add(true);
                                            System.out.println("Added " + currentVar + " to list of objectives. Goal is to maximize.");
                                        } else if (objTerms[i - 1].equals("+"))// minimize positive = minimize
                                        {
                                            objectivesMax.add(false);
                                            System.out.println("Added " + currentVar + " to list of objectives. Goal is to minimize.");
                                        }
                                    }
                                }
                            }
                        }

                        numObjectives = objectives.size();

                        // Now that we know which objectives are to be maximized and which are to be minimized, we can
                        // set the objective sense to our liking: maximize
                        objFunction.setSense(IloObjectiveSense.Maximize);

                        // update JPanel components now that we have this information
                        lblNumberOfObjectives.setEnabled(true);
                        textNumberOfObjectives.setText(Integer.toString(numObjectives));
                        textNumberOfObjectives.setEnabled(true);
                        lblAlphaDegrees.setEnabled(true);
                        textAlphaDegrees.setEnabled(true);
                        textAlphaDegrees.setEditable(true);
                        lblObjectiveIncrementSizes.setEnabled(true);
                        btnSetIncrementSizes.setEnabled(true);
                        guiCounter++;
                    } catch (IloException e) {
                        System.err.println("Concert Exception caught: " + e);
                    }
                    pleaseWait.setVisible(false);
                }
            });

            // What happens when user clicks button to set objective increments
            btnSetIncrementSizes.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    // instantiate array of objectives' delta values.
                    // Note: the increment for the first objective is arbitrarily set to zero
                    // (there is no increment measure for the first objective)
                    objDeltas = new double[numObjectives];
                    objDeltas[0] = 0.0;
                    lblObjDeltasStatus.setText("0 of " + (numObjectives - 1) + " increment sizes entered");
                    // cycle through frames to attain these values
                    for (int i = 1; i < objDeltas.length; i++) {
                        objDeltas[i] = Supporting1_DeltaFrame.getObjectiveDelta(objectives.get(i));
                        System.out.println("Delta value for " + objectives.get(i) + ": " + objDeltas[i]);
                        lblObjDeltasStatus.setText(i + "of " + (numObjectives - 1) + " increment sizes entered");
                    }

                    lblObjDeltasStatus.setText("All objective increment sizes set");
                    guiCounter++;
                    // Activate the RUN button if all req'd fields set
                    if (guiCounter == 3) btnRunAlphaDelta.setEnabled(true);
                }
            });
			
			/*
			 * Create the panel to hold CPLEX parameter information and add it as a tab
			 */
            // Future Release: layout set to grid format (num rows = num parameters to set in each subpanel, num cols = 2)
            JPanel panelCPLEXParams = new JPanel();
            // layout for the CPLEX settings tab
            panelCPLEXParams.setLayout(new BoxLayout(panelCPLEXParams, BoxLayout.PAGE_AXIS));
            // create the two boxed sections within the CPLEX settings tabs
            JPanel panelCPLEXTolerances = new JPanel();
            JPanel panelCPLEXOther = new JPanel();
            // set titled borders for the settings tabs
            panelCPLEXTolerances.setBorder(new TitledBorder("Tolerances"));
            panelCPLEXOther.setBorder(new TitledBorder("Computation Settings"));
            // add these two sections to the CPLEX settings tab
            panelCPLEXParams.add(panelCPLEXTolerances);
            panelCPLEXParams.add(panelCPLEXOther);
            // add the CPLEX settings tab to the window pane
            tabbedPane.addTab("CPLEX parameters", null, panelCPLEXParams, null);
            // add content to the boxed sections on the CPLEX settings tab:
            JLabel lblTimeLimit = new JLabel("Time Limit (seconds)");
            panelCPLEXOther.add(lblTimeLimit);
            textTimeLimit = new JTextField();
            panelCPLEXOther.add(textTimeLimit);
            textTimeLimit.setColumns(10);
            textTimeLimit.setText("120");

            JLabel lblIntegralityGap = new JLabel("Integrality Gap");
            panelCPLEXTolerances.add(lblIntegralityGap);
            textIntegralityGap = new JTextField();
            panelCPLEXTolerances.add(textIntegralityGap);
            textIntegralityGap.setColumns(10);

            JLabel lblOptimalityGap = new JLabel("Optimality Gap");
            panelCPLEXTolerances.add(lblOptimalityGap);
            textOptimalityGap = new JTextField();
            panelCPLEXTolerances.add(textOptimalityGap);
            textOptimalityGap.setColumns(10);

            JLabel lblWorkingMemory = new JLabel("Working Memory");
            panelCPLEXOther.add(lblWorkingMemory);
            textWorkingMemory = new JTextField();
            textWorkingMemory.setText("1000");
            panelCPLEXOther.add(textWorkingMemory);
            textWorkingMemory.setColumns(10);

            JLabel lblNumberOfThreads = new JLabel("Number of threads");
            panelCPLEXOther.add(lblNumberOfThreads);
            textNumberOfThreads = new JTextField();
            panelCPLEXOther.add(textNumberOfThreads);
            textNumberOfThreads.setColumns(10);
			
			
			/*
			 * When the user hits the RUN button...
			 * Future Release:
			 * 		Have instance of the frame (input-taking) object and of the AD algorithm (problem-solving) object.
			 * 		Attained information from the frame is passed to the fields of the AD algo. object.
			 * 		Hitting the RUN button calls a "run the algorithm" method in the AD algo. object.
			 */

            btnRunAlphaDelta.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        // Future release: error handling for incompatible parameter values
                        // Set CPLEX settings if the user has provided them. O/w use default.
                        if (textOptimalityGap.getText().length() > 0) {
                            cpxOptimalityGap = Double.parseDouble(textOptimalityGap.getText());
                            cplex.setParam(IloCplex.DoubleParam.EpGap, cpxOptimalityGap);
                        }

                        if (textNumberOfThreads.getText().length() > 0) {
                            cpxNumThreads = Integer.parseInt(textNumberOfThreads.getText());
                            cplex.setParam(IloCplex.IntParam.Threads, cpxNumThreads);
                        }

                        if (textWorkingMemory.getText().length() > 0) {
                            cpxWorkingMem = Double.parseDouble(textWorkingMemory.getText());
                            cplex.setParam(IloCplex.DoubleParam.WorkMem, cpxWorkingMem);
                        }

                        if (textIntegralityGap.getText().length() > 0) {
                            cpxIntegralityGap = Double.parseDouble(textIntegralityGap.getText());
                            cplex.setParam(IloCplex.DoubleParam.EpInt, cpxIntegralityGap);
                        }

                        if (textTimeLimit.getText().length() > 0) {
                            cpxTimeLimit = Double.parseDouble(textTimeLimit.getText());
                            cplex.setParam(IloCplex.DoubleParam.TiLim, cpxTimeLimit);
                        }
                        // Convert user's alpha parameter to radians for use in trig. functions
                        alphaDegrees = Double.parseDouble(textAlphaDegrees.getText());
                        alphaRadians = Math.toRadians(alphaDegrees);
						
						/*
						 * Construct Ideal Solutions
						 */

                        // Future Release: Add error handling for if objectives are not in conflict with one another
                        // (no conflict if an obj's ideal solution is obtained in more than one of the following
                        // single-obj. optimization problems performed below)
                        double[][] idealSolutions = new double[objectives.size()][objectives.size()];
                        objFunction = cplex.getObjective();

                        // get ideal solution for each objective
                        for (int i = 0; i < objectives.size(); i++) {
                            String objName = objectives.get(i);
                            boolean boolObjMax = objectivesMax.get(i);

                            String logname = "LOG_IdealSolution_" + objName + ".txt";
                            String outputFileAndPath = outputDestination + logname;
                            PrintStream outputStream = new PrintStream(new File(outputFileAndPath));
                            cplex.setOut(outputStream);

                            // set objective function coefficients
                            for (int j = 0; j < numCols; j++) {
                                if (lpMatrix.getNumVar(j).getName().equals(objName)) {
                                    if (boolObjMax) cplex.setLinearCoef(objFunction, lpMatrix.getNumVar(j), 1.0);
                                    else cplex.setLinearCoef(objFunction, lpMatrix.getNumVar(j), -1.0);
                                } else {
                                    cplex.setLinearCoef(objFunction, lpMatrix.getNumVar(j), 0.0);
                                }
                            }

                            // Start optimization for the current objective
                            // Future Release:
                            // Updates such as the one below that are printed to the console
                            // will instead be provided in some sort of status window that
                            // appears over (or perhaps in place of) the input GUI
                            System.out.println("Finding ideal solution for " + objName + "...");
                            System.out.println("Objective function: " + cplex.getObjective());
                            if (cplex.solve()) {
                                // write solution
                                cplex.writeSolution(outputDestination + "SOLN_IdealSolution_" + objName + ".sol");
                                // print ideal value out to user
                                System.out.println("Ideal value for " + objName + ": " + Math.abs(cplex.getObjValue()));
                                // record variable values
                                double[] singleObjSolutions = cplex.getValues(lpMatrix);
                                // record objective values
                                for (int j = 0; j < objectives.size(); j++) {
                                    String currentObjName = objectives.get(j);

                                    // cycle through the variable values from the solution
                                    for (int k = 0; k < numCols; k++) {
                                        // if the variable name is equal to the current objective's name
                                        if (lpMatrix.getNumVar(k).getName().equals(currentObjName)) {
                                            // then we store the value of that variable in our ideal array
                                            idealSolutions[i][j] = singleObjSolutions[k];
                                            // here,	i is the index of the objective whose ideal solution we have obtained
                                            // 			j is the index of the objective whose value we are currently recording
                                            //			k is the index of objective j in CPLEX's list of variables
                                            // (diagonals of the idealSolutions array are the ideal solutions for each objective)
                                            break;
                                        }
                                    }
                                }
                            }

                            // conclude logfile
                            cplex.setOut(null);
                        }

                        System.out.println("Ideal solution constructed.");
                        String printThisLine = objectives.get(0);
                        for (int i = 1; i < objectives.size(); i++) {
                            printThisLine += ", " + objectives.get(i);
                        }
                        System.out.println("Variables: " + printThisLine);
                        printThisLine = Double.toString(idealSolutions[0][0]);
                        for (int i = 1; i < objectives.size(); i++) {
                            printThisLine += ", " + Double.toString(idealSolutions[i][i]);
                        }
                        System.out.println("Ideal Values: " + printThisLine);
											
						/*
						 * End construction of ideal solution.
						 */
									
						
						/*
						 * Begin detection of worst case (upper bound for minimization objectives, 0.0 for maximization objectives)
						 */

                        double[] objWorstCase = new double[objectives.size()];

                        for (int i = 0; i < objectives.size(); i++) {
                            boolean boolObjMax = objectivesMax.get(i);
                            if (boolObjMax) objWorstCase[i] = getMinValueOfArrayColumn(idealSolutions, i);
                            else objWorstCase[i] = getMaxValueOfArrayColumn(idealSolutions, i);
                        }
						
						/*
						 * End detection of worst case (upper bound for minimization objectives, 0.0 for maximization objectives)
						 */
						
						/*
						 * Begin Pre-Alpha Delta Preparations
						 */

                        // zero objective function coefficients
                        for (int i = 0; i < numCols; i++) cplex.setLinearCoef(objFunction, lpMatrix.getNumVar(i), 0.0);

                        // define objective function weights sans normalization by ideal values
                        double otherWeightNonNorm = Math.tan(alphaRadians) / (1 + Math.tan(alphaRadians));
                        double mainWeightNonNorm = (1 - (objectives.size() - 1) * otherWeightNonNorm);
                        // assign objective function weights (normalized by largest value objective takes)
                        for (int i = 0; i < objectives.size(); i++) {
                            double weight = 0.0;
                            // initialize index to value that we cannot record to (prevent misassignment
                            // of weights by instead forcing error)
                            int objIndex = -4;
                            // find the current objective in the LP matrix
                            for (int j = 0; j < numCols; j++) {
                                if (lpMatrix.getNumVar(j).getName().equals(objectives.get(i))) {
                                    objIndex = j;
                                    // the first objective is different (greater weight (in absolute value at least))
                                    if (i == 0) {
                                        if (objectivesMax.get(i)) weight = mainWeightNonNorm / idealSolutions[i][i];
                                        else weight = -mainWeightNonNorm / objWorstCase[i];
                                    }
                                    // for all other weights
                                    else {
                                        if (objectivesMax.get(i)) weight = otherWeightNonNorm / idealSolutions[i][i];
                                        else weight = -otherWeightNonNorm / objWorstCase[i];
                                    }
                                    break;
                                }
                            }
                            cplex.setLinearCoef(objFunction, lpMatrix.getNumVar(objIndex), weight);
                        }
                        System.out.println("Objective function used to generate efficient frontier:");
                        System.out.println(objFunction);

                        // List to hold indicator variables we will introduce each iteration
                        ArrayList<IloNumVar> newVariables = new ArrayList<IloNumVar>();
                        // counter for these variables
                        int counterNewVariables = 0;

                        // counter to identify current solution number
                        int counterSolutionNum = 0;

                        // array to hold index of each objective
                        int[] objectiveIndex = new int[objectives.size()];
                        // store the objective indices...
                        for (int i = 0; i < objectives.size(); i++) {
                            for (int j = 0; j < numCols; j++) {
                                if (lpMatrix.getNumVar(j).getName().equals(objectives.get(i))) {
                                    objectiveIndex[i] = j;
                                    break;
                                }
                            }
                        }

                        // array to hold solution values of  objectives throughout the alpha-delta implementation
                        double[] objSolutionValue = new double[objectives.size()];
                        // first line of solutions contains worst case for the variables
                        for (int i = 0; i < objectives.size(); i++) objSolutionValue[i] = objWorstCase[i];

                        // create output stream for solutions file and generate first lines of file: best case, worst case, column headers
                        String solutionOutputFileName = "OptimalSolutions.txt";
                        PrintStream optimalSolutionsWriter = new PrintStream(new File(outputDestination + solutionOutputFileName));
                        // printing ideal solution
                        printThisLine = "Ideal Solution: " + printThisLine;
                        optimalSolutionsWriter.println(printThisLine);
                        // printing worst case solution
                        for (int i = 0; i < objWorstCase.length; i++) {
                            if (i == 0) printThisLine = Double.toString(objWorstCase[i]);
                            else printThisLine += ", " + Double.toString(objWorstCase[i]);
                        }
                        printThisLine = "Worst Case Values: " + printThisLine;
                        optimalSolutionsWriter.println(printThisLine);
                        // printing column headers
                        printThisLine = "SolutionIndex";
                        for (int i = 0; i < objectives.size(); i++) {
                            printThisLine += ", " + objectives.get(i);
                        }
                        optimalSolutionsWriter.println(printThisLine);

                        // holder for number of variables (columns) throughout the AD algorithm
                        int numColsNow;
						/*
						 * End Pre-Alpha Delta Preparation
						 */
						
						/*
						 * Begin Alpha Delta Algorithm
						 */

                        // determine whether current objective values are sufficient
                        // 	to begin the algorithm
                        // Future release: Add error handling here for if the objectives' values do
                        // 					not cause us to enter the loop. (Obj's not in conflict)
                        // Future release: Add ability to set initial variable values.
                        // (these user-provided values would be input to the objSolutionValue array)
                        // This would enable the user to build the frontier starting
                        // at a value previously obtained in another run
                        // Note: Would require Pareto efficient (or at least feasible) point to begin with
                        // Future release also to include error handling for if the point provided by the user
                        // is infeasible or not efficient
                        boolean go = false;
                        int numReqConditions = objectives.size() - 1;
                        int conditionChecker;

                        conditionChecker = 0;
                        for (int i = 1; i < objectives.size(); i++) {
                            if (objectivesMax.get(i)) {
                                boolean condition = (objSolutionValue[i] + objDeltas[i] < idealSolutions[i][i]);
                                if (condition) conditionChecker++;
                            } else {
                                boolean condition = (objSolutionValue[i] - objDeltas[i] > idealSolutions[i][i]);
                                if (condition) conditionChecker++;
                            }
                        }
                        if (conditionChecker == numReqConditions) go = true;


                        while (go) {
                            // Begin logfile for solution (point on efficient frontier)
                            String logname = "LOG_Solution_" + counterSolutionNum + ".txt";
                            String outputFileAndPath = outputDestination + logname;
                            PrintStream outputStream = new PrintStream(new File(outputFileAndPath));
                            cplex.setOut(outputStream);

                            // For debug-purposes only: print problem file for the first 5 iterations:
                            // if (counterSolutionNum < 5) cplex.exportModel(outputDestination + "AAA_ProblemChecker_" + counterSolutionNum + ".lp");

                            // Pre-optimization: Because of issues with CPLEX optimizing its current problem object,
                            // 		we export the model, clear it from the CPLEX object, and reinstate it
                            cplex.exportModel(outputDestination + "TEMP_ProblemFile.lp");
                            cplex.clearModel();
                            cplex.importModel(outputDestination + "TEMP_ProblemFile.lp");
                            // This wiping and reinstating means that we must reinstate the lpMatrix object
                            lpMatrix = (IloLPMatrix) cplex.LPMatrixIterator().next();

                            // Optimize
                            if (cplex.solve()) {
                                System.out.println("New solution found on efficient frontier.");

                                // write solution file
                                cplex.writeSolution(outputDestination + "Soln_FrontierPoint_" + counterSolutionNum + ".sol");

                                // record solution
                                double[] solutionValues = cplex.getValues(lpMatrix);
                                for (int i = 0; i < objectives.size(); i++) {
                                    objSolutionValue[i] = solutionValues[objectiveIndex[i]];
                                }

                                // write objective values to file (and console output)
                                // Future Release: On window that is displayed to show algorithm' progress
                                // 					display the number of solutions created and the most recent values
                                printThisLine = counterSolutionNum + ", " + objSolutionValue[0];
                                for (int i = 1; i < objSolutionValue.length; i++)
                                    printThisLine += ", " + objSolutionValue[i];
                                System.out.println(printThisLine);
                                optimalSolutionsWriter.println(printThisLine);

                                // Solution obtained, reported, and stored. Update problem for next iteration
                                // Linear Numerical Expression: the constraint specifying that the new indicator variables must have sum equal to 1
                                IloLinearNumExpr constraint0 = cplex.linearNumExpr();

                                // create new variables and add them to the LP Matrix
                                for (int i = 0; i < objectives.size() - 1; i++) {
                                    // new variable index/subscript
                                    int counterNewVariablesi = counterNewVariables + i;
                                    // create the new variable and add it to ArrayList
                                    newVariables.add(cplex.boolVar("y_" + counterNewVariablesi));
                                    // add it to CPLEX's LP matrix
                                    lpMatrix.addColumn(newVariables.get(newVariables.size() - 1));
                                    // add it to the constraint requiring objective improvement
                                    constraint0.addTerm(1.0, newVariables.get(newVariables.size() - 1));
                                }
                                // all terms now added to the constraint requiring objective improvement. Polishing it off:
                                cplex.addEq(constraint0, 1.0);

                                // update solution counter
                                counterSolutionNum++;

                                // update new variable counter
                                counterNewVariables += objectives.size() - 1;

                                // record new number of columns after adding the new variables
                                numColsNow = cplex.getNcols();

                                // equality constraint for "improvement indicator variables" complete
                                // creating remaining constraints:
                                //(note - starting with the second objective, since the first objective does not require a new constraint)
                                for (int i = 1; i < objectives.size(); i++) {
                                    // constraint under construction
                                    IloLinearNumExpr constraint = cplex.linearNumExpr();

                                    // current objective info
                                    int objIndex = objectiveIndex[i];
                                    IloNumVar objNumVar = lpMatrix.getNumVar(objIndex);
                                    double worstCase = objWorstCase[i];
                                    int indicatorVariableIndex = numColsNow - objectives.size() + i;

                                    if (objectivesMax.get(i)) {
                                        // if the objective is maximization, we build a constraint such as:
                                        // obj2 >= (obj2PreviousValue + delta2)*y_0 + obj2LowerBound*y_1 + obj2LowerBound*y_2 + ...
                                        // or in proper CPLEX formatting
                                        // 1.0*obj2 - (obj2PrevVal + delta2)*y_0 - obj2LowerBound*y_1 - obj2LowerBound*y_2 - ... >= 0
                                        constraint.addTerm(1.0, objNumVar);
                                        constraint.addTerm(-(objSolutionValue[i] + objDeltas[i]), lpMatrix.getNumVar(indicatorVariableIndex));
                                        for (int j = 1; j < objectives.size(); j++) {
                                            // Adding the lower bound terms
                                            // We have a term for each indicator variable that is not associated with the current objective
                                            if (numColsNow - objectives.size() + j != indicatorVariableIndex) {
                                                constraint.addTerm(-worstCase, lpMatrix.getNumVar(numColsNow - objectives.size() + j));
                                            }
                                        }
                                        cplex.addGe(constraint, 0.0);
                                    } else {
                                        // if the objective is minimization, we have constraint such as
                                        // obj2 <= (obj2PreviousValue - delta2)*y_0 + obj2UpperBound*y_1 + obj2UpperBound*y_2 + ... (for all indicator variables)
                                        // or in proper CPLEX formatting
                                        // 1.0*obj2 - (obj2PreviousValue - delta2)*y_0 - obj2UpperBound*y_1 - obj2UpperBound*y_2 - ... <= 0
                                        constraint.addTerm(1.0, objNumVar);
                                        constraint.addTerm(-(objSolutionValue[i] - objDeltas[i]), lpMatrix.getNumVar(indicatorVariableIndex));
                                        for (int j = 1; j < objectives.size(); j++) {
                                            // Adding the upper bound terms
                                            // We have a term for each indicator variable that is not associated with the current objective
                                            if (numColsNow - objectives.size() + j != indicatorVariableIndex) {
                                                constraint.addTerm(-worstCase, lpMatrix.getNumVar(numColsNow - objectives.size() + j));
                                            }
                                        }
                                        cplex.addLe(constraint, 0.0);
                                    }
                                }

                                // check whether we are in a state to continue in the algorithm
                                conditionChecker = 0;
                                for (int i = 1; i < objectives.size(); i++) {
                                    if (objectivesMax.get(i)) {
                                        boolean condition = (objSolutionValue[i] + objDeltas[i] < idealSolutions[i][i]);
                                        if (condition) conditionChecker++;
                                    } else {
                                        boolean condition = (objSolutionValue[i] - objDeltas[i] > idealSolutions[i][i]);
                                        if (condition) conditionChecker++;
                                    }
                                }
                                if (conditionChecker == numReqConditions) go = true;
                            } else {
                                System.out.println("CPLEX could not optimize after iteration " + (counterSolutionNum - 1) + ". Algorithm terminated.");
                                go = false;
                            }

                            // conclude logfile
                            cplex.setOut(null);
                        }

                        System.out.println("Done with CPLEX");
                        System.exit(0);
						
						/*
						 * End Alpha Delta Implementation
						 */


                        //close any remaining output streams
                        if (optimalSolutionsWriter != null) optimalSolutionsWriter.close();


                        return;
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    } catch (IloException excep) {
                        System.err.println("Concert Exception caught: " + excep);
                        System.exit(0);
                    }
                }

                private double getMaxValueOfArrayColumn(double[][] array, int column) {
                    double maxValue = 0.0;
                    for (int i = 0; i < array.length; i++) {
                        if (array[i][column] > maxValue) maxValue = array[i][column];
                    }
                    return maxValue;
                }

                private double getMinValueOfArrayColumn(double[][] array, int column) {
                    double minValue = Double.MAX_VALUE;
                    for (int i = 0; i < array.length; i++) {
                        if (array[i][column] < minValue) minValue = array[i][column];
                    }
                    return minValue;
                }

            });
        } catch (IloException excep) {
            System.err.println("Concert Exception caught: " + excep);
            System.exit(0);
        }
    }
}
