

This is the base version of modeling strategic voting in dynamic environment based on Jadex platform (version:**3.0.117**). 


There are two types of BDI agents. One is the VoterBDI which is responsible for expressing opinions over some candidates in sobiable and dynamic environments, the other is the CollectorBDI which is a center agent for collecting all the submitted ballots. An agent is allowed to update its ballot before the termination of the voting process. In the base version, the agent will turn to support the candidate with the majority votes based on the score profile which is gained via its neighborhood, if such promissing candidate exists; otherwise, it will persist in casting ballot to its favorite candidate. The well-known Plurality rule is used to decide the winner in the CollectorBDI agent. 



The VoterBDI agent achieves the following functions:

1. when a new agent joins the voting process, all of its existing neighbors are informed, and add it as a neighbor,
2. when an existing agent leaves, all of its neighbors drop it from their neighborhood,
3. existing agents can add or delete other agents as neighbors,
4. when the neighbor relationship  of an agent changes, the agent will and updates its score profile and notify its corresponding neighbor to add or delete it, 
5. when the score profile of an agent changes, the agent will reconsider to change its ballot,
6. when an agent has changed its ballot, it will notify all of its neighbors to update their score profiles,
7. when an agent submits its ballot, it will notify the CollectBDI agent to collect that ballot.

Here is a flow chart of the above functions.
![the collectorBDI](https://github.com/lxx886/voting/blob/main/images/the%20voterBDI.bmp)



The CollectorBDI agent achieves the following functions:
1. collect all the submitted ballot,
2. determine the winner immediately when the total score profile changes.

Here is a flow chart of the above functions.

![the collectorBDI](https://raw.githubusercontent.com/lxx886/voting/main/images/the%20CollectBDI.bmp)
