#
# CSCI E-66: Problem Set 1, SQL Programming Problems
#

#
# For each problem, use a text editor to add the appropriate SQL
# command between the triple quotes provided for that problem's variable.
#
# For example, here is how you would include a query that finds the
# names and years of all movies in the database with an R rating:
#
sample = """
         SELECT name, year
         FROM Movie
         WHERE rating = 'R';
         """

#
# Problem 5. Put your SQL command between the triple quotes found below.
#
problem5 = """
			SELECT name, pob, dob
			FROM Person
			WHERE name = 'Scarlett Johansson' OR name = 'Renee Zellweger';
           """

#
# Problem 6. Put your SQL command between the triple quotes found below.
#
problem6 = """
			SELECT name, O.year 
			FROM Movie AS M, Oscar AS O 
			WHERE M.id = O.movie_id 
			AND O.year >= 2010 
			AND O.year <= 2019 
			ORDER BY O.year DESC;
           """

#
# Problem 7. Put your SQL command between the triple quotes found below.
#
problem7 = """
			SELECT O.year, M.name
			FROM Oscar as O, Movie as M
			WHERE O.movie_id = M.id
			AND type = 'BEST-DIRECTOR'
			AND O.person_id IN 
			(SELECT id from Person WHERE name = 'Martin Scorsese');
           """

#
# Problem 8. Put your SQL command between the triple quotes found below.
#
problem8 = """
           SELECT COUNT(pob)
		   FROM Person
		   WHERE pob IS NOT NULL 
		   AND pob NOT LIKE '%USA';
           """

#
# Problem 9. Put your SQL command between the triple quotes found below.
#
problem9 = """
           SELECT name, runtime
		   FROM Movie
		   WHERE genre LIKE '%N%'
		   AND runtime = 
		   (SELECT MAX(runtime) 
		   FROM Movie
		   WHERE genre LIKE '%N%');
           """

#
# Problem 10. Put your SQL command between the triple quotes found below.
#
problem10 = """
            SELECT M.name, COUNT(type)
			FROM Oscar as O, Movie as M
			WHERE O.movie_id = M.id
			GROUP BY M.name
			HAVING COUNT(type) >= 4;
            """

#
# Problem 11. Put your SQL command between the triple quotes found below.
#
problem11 = """
            SELECT P.name, P.pob
			FROM Person as P, Director as D
			where P.id = D.director_id
			AND D.director_id IN
			(SELECT id
			FROM Person 
			WHERE pob like '%France');
            """

#
# Problem 12. Put your SQL command between the triple quotes found below.
#
problem12 = """
            SELECT M.earnings_rank, M.name, O.type
			FROM Movie as M left join Oscar as O 
			ON M.id = O.movie_id
			WHERE M.earnings_rank <= 25
			ORDER BY earnings_rank;
            """

#
# Problem 13. Put your SQL command between the triple quotes found below.
#
problem13 = """
			SELECT COUNT(runtime)
			FROM Movie as M, Oscar as O
			WHERE M.id = O.movie_id
			AND O.type = 'BEST-PICTURE'
			AND M.runtime > 
			(SELECT AVG(runtime)
			FROM Movie);           
            """

#
# Problem 14. Put your SQL command between the triple quotes found below.
#
problem14 = """
			SELECT O.type, P.name, M.name
			FROM Oscar as O LEFT JOIN Person as P on O.person_id = P.id, Movie as M
			WHERE O.movie_id = M.id 
			AND O.year = '1990';            
            """

#
# Problem 15. Put your SQL command between the triple quotes found below.
#
problem15 = """
			SELECT COUNT(person_id) from (SELECT person_id
							FROM Oscar 
							WHERE type = 'BEST-SUPPORTING-ACTOR'
							AND person_id NOT IN
								(SELECT person_id 
								FROM Oscar 
								WHERE type = 'BEST-ACTOR')
							UNION ALL 
							SELECT person_id
							FROM Oscar 
							WHERE type = 'BEST-SUPPORTING-ACTRESS'
							AND person_id NOT IN
								(SELECT person_id 
								FROM Oscar 
								WHERE type = 'BEST-ACTRESS')); 	          
            """

#
# Problem 16. Put your SQL command between the triple quotes found below.
#
problem16 = """
			UPDATE Movie
			SET earnings_rank = '310'
			WHERE name = 'Casino Royale';           
            """

#
# Problem 17. Put your SQL command between the triple quotes found below.
#
problem17 = """
			SELECT P.name, O1.type, O1.year AS year1, O2.year AS year2
			FROM Oscar O1, Oscar O2, Person as P
			WHERE O1.person_id = O2.person_id
			AND P.id = O1.person_id
			AND (year2 - year1) = 1
			ORDER BY P.name;          
            """

#
# Problem 18 (required for grad-credit students; optional for others). 
# Put your SQL command between the triple quotes found below.
#
problem18 = """
			select O.type, count(A.movie_id) AS movie_count
			from Oscar As O inner join Actor AS A on O.person_id = A.actor_id
			where actor_id in
			(select DISTINCT person_id
			from Oscar 
			where type = 'BEST-SUPPORTING-ACTRESS'
			OR type = 'BEST-ACTRESS'
			OR type = 'BEST-ACTOR'
			OR type = 'BEST-SUPPORTING-ACTOR')
			GROUP BY O.type;            
            """

#
# Problem 19 (required for grad-credit students; optional for others). 
# Put your SQL command between the triple quotes found below.
#
problem19 = """
            
            """
