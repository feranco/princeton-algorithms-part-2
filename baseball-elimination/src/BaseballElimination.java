import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BaseballElimination {

    private final int teams;
    private  String leader;
    private final int[] wins;
    private final int[] losses;
    private final int[] remaining;
    private final int[][] games;
    private final Map<String, Integer> teamToId;

    private boolean isTriviallyEliminated(int id) {
	return (wins[id] + remaining[id] < wins[teamToId.get(leader)]) ? true : false;
    }

    // @id = candidate to be eliminated
    private FlowNetwork buildFlowNetwork(int id) {
	
	int numGames = (teams * (teams - 1)) / 2; // number of games	
	int vertices = numGames + teams + 2; // number of vertices = number of games + number of teams + source + sink
	int source = 0; // source = first ID
	int sink = vertices - 1; // target = last ID
	
	FlowNetwork flowNetwork = new FlowNetwork(vertices);

	int gameVertex = 1; //id of first game vertex
	//cycle on all teams (gamesRow) 
	for (int i = 0; i < teams; i++) {//games.length
	    if (i == id) continue;  //skip candidate
	    //cycle on all remaining teams (gamesCol) 
	    for (int j = i + 1; j < teams; j++) {//games[i].length
		if (j == id) continue; //skip candidate
		// add edges from source to games vertices
		flowNetwork.addEdge(new FlowEdge(source, gameVertex, games[i][j]));
		// add edges from games vertices to teams vertices (+ 1 is for the source)
		flowNetwork.addEdge(new FlowEdge(gameVertex, numGames + i + 1, Double.POSITIVE_INFINITY));
		flowNetwork.addEdge(new FlowEdge(gameVertex, numGames + j + 1, Double.POSITIVE_INFINITY));
		gameVertex++;
	    }
	    // add edges from teams vertices to target
	    int teamVertex = numGames + i + 1;
	    flowNetwork.addEdge(new FlowEdge(teamVertex, sink, Math.max(0, wins[id] + remaining[id] - wins[i])));
	}

	return flowNetwork;
    }
    
    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
	
	In in = new In(filename);
	int maxWins = 0;
        teams = in.readInt();
	
        wins = new int[teams];
        losses = new int[teams];
        remaining = new int[teams];
        games = new int[teams][teams];
        teamToId = new HashMap<String, Integer>();

	for (int i = 0; i < teams; i++) {
            String team = in.readString();
            teamToId.put(team, i);
            wins[i] = in.readInt();
            losses[i] = in.readInt();
            remaining[i] = in.readInt();

            for (int j = 0; j < teams; j++) {
                games[i][j] = in.readInt();
            }

	    if (wins[i] > maxWins) {
                maxWins = wins[i];
                leader = team;
            }
        }
    }

    // number of teams
    public int numberOfTeams()  {
	return teams;
    }

    // all teams
    public Iterable<String> teams()   {
	return teamToId.keySet();
    }
    
    // number of wins for given team
    public int wins(String team)   {
	if (!teamToId.containsKey(team)) {
            throw new IllegalArgumentException("The team is not valid!");
        }
        return wins[teamToId.get(team)];
    }
    // number of losses for given team
    public int losses(String team) {
	if (!teamToId.containsKey(team)) {
            throw new IllegalArgumentException("The team is not valid!");
        }
        return losses[teamToId.get(team)];
    }

    // number of remaining games for given team
    public int remaining(String team)   {
	if (!teamToId.containsKey(team)) {
            throw new IllegalArgumentException("The team is not valid!");
        }
        return remaining[teamToId.get(team)];
    }
    
    // number of remaining games between team1 and team2
    public int against(String team1, String team2)   {
	if (!teamToId.containsKey(team1) || !teamToId.containsKey(team2)) {
	    throw new IllegalArgumentException("The teams are not valid!");
	}
	return games[teamToId.get(team1)][teamToId.get(team2)];
    }

    // is given team eliminated?
    public boolean isEliminated(String team)             {
	if (!teamToId.containsKey(team)) {
            throw new IllegalArgumentException("The team is not valid!");
        }

	int id = teamToId.get(team);

	// check for trivial elimination
	if (isTriviallyEliminated(id)) {
            return true;
        }

	// check for elimination: team is eliminated if a flow from source to games vertices is not
	// equal to the capacity (not all game can be played without eliminating the input team
	FlowNetwork flowNetwork = buildFlowNetwork(id);
	int source = 0;
	int sink = flowNetwork.V() - 1;
	FordFulkerson ff = new FordFulkerson(flowNetwork, source, sink);
	for (FlowEdge edge : flowNetwork.adj(source)) {
	    if (edge.flow() < edge.capacity()) return true;	    
	}
	
	return false;
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team)  {
	if (!teamToId.containsKey(team)) {
            throw new IllegalArgumentException("The team is not valid!");
        }

	Set<String> set = new HashSet<>();

	if (isTriviallyEliminated(teamToId.get(team))) {
	    set.add(leader);
            return set;
        }
	
	FlowNetwork flowNetwork = buildFlowNetwork(teamToId.get(team));
	int source = 0;
	int sink = flowNetwork.V() - 1;
	FordFulkerson ff = new FordFulkerson(flowNetwork, 0, sink);

	for (FlowEdge edge : flowNetwork.adj(source)) {
            if (edge.flow() < edge.capacity()) {
                for (String t : teams()) {
                    int teamId = teamToId.get(t);
                    if (ff.inCut(teamId)) {
                        set.add(t);
                    }
                }
            }
        }
        return set.isEmpty() ? null : set;
    }
}
