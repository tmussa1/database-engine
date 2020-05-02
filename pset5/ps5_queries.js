/* 
 * Problem Set 5, MongoDB Programming Problems
 */

/*********************************************************************
 * REMEMBER:
 *  1. For each problem, you should assign your MongoDB method call 
 *     to the variable called "results" that we have provided. 
 *     Follow the model shown in the sample query below.
 *  2. You should *not* make any other additions or modifications to
 *     this file.
 *  3. You should test that the queries in this file are correct by
 *     executing all of the queries in the file from the command line.
 *     See the assignment for more details.
 *********************************************************************/
db = db.getSiblingDB('imdb')

/*
 * Sample query: Find the names of all movies in the database from 1990.
 */

print()
print("results of sample query")
print("-----------------------")

results0 = db.movies.find( { year: 1990 }, 
                           { name: 1, _id: 0 } )
results0.forEach(printjson)


/*
 * Query 1. Put your method call for this query below,
 * assigning it to the results variable that we have provided.
 */

print()
print("results of query 1")
print("-------------------")

results1 = db.movies.find(	{year: 2000, rating: "PG-13"}, 
							{name: 1, runtime: 1, _id: 0});

results1.forEach(printjson)


/*
 * Query 2. Put your method call for this query below,
 * assigning it to the results variable that we have provided.
 */

print()
print("results of query 2")
print("-------------------")

results2 = db.people.find(	{$or:[{name: "Mahershala Ali"}, {name: "Regina King"}]}, 
								{name: 1, pob: 1, dob: 1, _id: 0});

results2.forEach(printjson)


/*
 * Query 3. Put your method call for this query below,
 * assigning it to the results variable that we have provided.
 */

print()
print("results of query 3")
print("-------------------")

results3 = db.oscars.aggregate({$match: {$and: [{year: {$gte: 1990}}, {year: {$lte: 1999}}], 
										type: "BEST-PICTURE"}},
										{$project: {_id: 0, year: 1, name: "$movie.name"}});

results3.forEach(printjson)


/*
 * Query 4. Put your method call for this query below,
 * assigning it to the results variable that we have provided.
 */

print()
print("results of query 4")
print("-------------------")

results4 = db.people.aggregate(	{$match: {hasDirected: {$exists: true}, pob: /, France$/i}},
								{$project: {_id: 0, name: 1, pob: 1}});

results4.forEach(printjson)


/*
 * Query 5. Put your method call for this query below,
 * assigning it to the results variable that we have provided.
 */

print()
print("results of query 5")
print("-------------------")

results5 = db.movies.aggregate(	{$match: {rating: {$exists: true}}},
								{$group: {_id: "$rating", average_runtime: {$avg: "$runtime"}}},
								{$project: {average_runtime: 1}});

results5.forEach(printjson)


/*
 * Query 6. Put your method call for this query below,
 * assigning it to the results variable that we have provided.
 */

print()
print("results of query 6")
print("-------------------")

results6 = db.movies.aggregate(	{$match: {rating: {$exists: true}}},
								{$group: {_id: "$rating", average_runtime: {$avg: "$runtime"}}},
								{$project: {rating: "$_id", average_runtime: 1, _id: 0}});

results6.forEach(printjson)


/*
 * Query 7. Put your method call for this query below,
 * assigning it to the results variable that we have provided.
 */

print()
print("results of query 7")
print("-------------------")

results7 = db.oscars.aggregate(	{$match: {}},
								{$group: {_id: "$movie.name", count: {$sum: 1}}},
								{$match: {count: {$eq: 4}}},
								{$project: {name: "$_id", _id: 0}});

results7.forEach(printjson)


/*
 * Query 8. Put your method call for this query below,
 * assigning it to the results variable that we have provided.
 */

print()
print("results of query 8")
print("-------------------")

results8 = db.movies.distinct("actors.name", {"directors.name": "Steven Spielberg"});

results8.forEach(printjson)


/*
 * Query 9. Put your method call for this query below,
 * assigning it to the results variable that we have provided.
 */

print()
print("results of query 9")
print("-------------------")

results9 = db.movies.aggregate(	{$match: {genre: {$eq: "N"}}},
								{$sort:	{runtime: -1}},
								{$limit: 1},
								{$project: {name: 1, _id: 0}});

results9.forEach(printjson)


/*
 * Query 10. Put your method call for this query below,
 * assigning it to the results variable that we have provided.
 */

print()
print("results of query 10")
print("--------------------")

results10 = 

results10.forEach(printjson)


/*
 * Query 11. Put your method call for this query below,
 * assigning it to the results variable that we have provided.
 *
 * required for grad-credit students; optional for others
 */

print()
print("results of query 11")
print("----------------------")

results11 = 

results11.forEach(printjson)
