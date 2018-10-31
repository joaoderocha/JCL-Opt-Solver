# JCL-Opt-Solver
A Solver wich uses Javacaela to help optimization with multi cores and multi computers, also know as clusters.

Config.properties backtracking stable execution

mode = kernel.mode.BtMultipleBranchParallelism
load = user.load_input.CoordinatesXY
upperlower = user.lower_upper_calculus.UpperCalculusNearestNeighbor
searchstrategy = kernel.search_strategy.TaskDecreaseUpperBound
evaluation = user.evaluation.DirectPermutationalEvaluation
pruning = user.pruning.RemainingVerticesPruning;user.pruning.CrossLinesEuclideanPruning;user.pruning.LowerThanUpperPruning
edgecalculus = user.edge_calculus.StaticCalculus
vars = distances
timeout = 5000
verify = 10
log = false

Config.properties permutation stable execution** not final

load = tsp_jcl.kernel.load_input.DistanceMatrix
upperlower = tsp_jcl.user.lower_upper_calculus.LowerBoundCalculusExemple
searchstrategy = tsp_jcl.kernel.search_strategy.TspTaskPermutationalWalkthrough
evaluation = tsp_jcl.user.evaluation.DirectPermutationalEvaluation
edgecalculus = tsp_jcl.user.edge_calculus.StaticCalculus
pruning = tsp_jcl.user.pruning.RotationTestPruning
vars = DataStruct
timeout = 5000
verify = 10
log = false
