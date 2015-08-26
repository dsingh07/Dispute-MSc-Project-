# create an initial set of arguments and attack relations
arguments = [1, 2, 3, 4, 5]
attacks = [[2], [3, 4], [4], [3, 5], []]

# Function to select next valid and legal move from user input
def get_choice(choices):
  choice = ""
  while choice not in choices:
	  try:
		choice = int(raw_input("Make your move: "))
	  except ValueError:
		print ("Incorrect format")
  return choice

# print attack relations to console
for i in range(0, len(arguments)-1):
	for k in range(0, len(attacks[i])):
		print str(i+1) + " is attacked by " + str(attacks[i][k])
		
# creation of lists to store current moves and possible legal moves
pmoves = []
legal_pmoves = []
for i in arguments:
	legal_pmoves.append(i)
omoves = []
legal_omoves = []
dispute = []

# initialise variable to store last argument moved
last = 0

print "\n********************************************"

# algorithm defining sequence of moves in game
for i in range(0, 2*(len(arguments))):
	# initial proponent moves
	if i == 0:
		print "\n***** Proponent moves first *****"
		print "Legal arguments: " + str(legal_pmoves)
		pmove = get_choice(legal_pmoves)
		# add current move to moves list and set to last argument moved
		pmoves.append(pmove)
		last = int(pmove)
		print last
		dispute.append("P-" +str(last))
		i = i+1
	# remaining proponent moves
	elif i % 2 == 0:
		print "\n***** Proponent moves *****"
		print "Already moved: " + str(pmoves)
		legal_pmoves = []
		# add legal moves including only those defending against preceding attack
		for k in attacks[last-1]:
			legal_pmoves.append(k)
		# remove legal move if already moved since proponent cannot repeat moves
		for j in legal_pmoves:
			if j in pmoves: legal_pmoves.remove(j)
		# EXTRA EFFICIENCY RULES
		# remove from legal moves if
		# move is attacked by argument that move is against
		for l in legal_pmoves:
			if last in attacks[int(l)-1]:
				legal_pmoves.remove(l)
		# move is attacked by itself
		for l in legal_pmoves:
			if l in attacks[int(l)-1]:
				legal_pmoves.remove(l)
		# attacks or is attacked by a move that proponent has already moved
		for l in legal_pmoves:
			for x in pmoves:
				if (x in attacks[int(l)-1]) or (l in attacks[x-1]):
					legal_pmoves.remove(l)
		# remove duplicates
		legal_pmoves = list(set(legal_pmoves))
		print "Legal arguments: " + str(legal_pmoves)
		# end game if no more legal moves
		if not legal_pmoves:
			print "No more legal moves"
			print "Opponent has won the game"
			print dispute
			break
		pmove = get_choice(legal_pmoves)
		pmoves.append(pmove)
		last = pmove
		print last
		dispute.append("P-" +str(last))
		i = i+1
	# opponent moves
	elif i % 2 == 1:
		print "\n***** Opponent moves *****"
		print "Already moved: " + str(omoves)
		legal_omoves = []
		# add legal moves including only those attacking last moved argument
		for k in attacks[last-1]:
			legal_omoves.append(k)
		print "Legal arguments: " + str(legal_omoves)
		# end game if no more legal moves
		if not legal_omoves:
			print "No more legal moves"
			print "Proponent has won the game"
			print dispute
			break
		omove = get_choice(legal_omoves)
		omoves.append(omove)
		last = omove
		print last
		dispute.append("O-" +str(last))
		i = i+1