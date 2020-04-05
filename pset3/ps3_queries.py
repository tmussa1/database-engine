#
# CSCI E-66: Problem Set 3: XQuery Programming Problems
#

#
# For each query, use a text editor to add the appropriate XQuery
# command between the triple quotes provided for that query's variable.
#
# For example, here is how you would include a query that finds
# the names of all movies in the database from 1990.
#
sample = """
    for $m in //movie
    where $m/year = 1990
    return $m/name
"""

#
# 1. Put your query for this problem between the triple quotes found below.
#    Follow the same format as the model query shown above.
#
query1 = """
    //person[ends-with(dob, "04-01")]/name
"""

#
# 2. Put your query for this problem between the triple quotes found below.
#
query2 = """
    for $p in //person
    let $bdate := $p/dob
    where substring($bdate, 6) = '04-01'
    order by $bdate
    return <april-first>{$p/name/text(), ' (', $bdate/text() , ')'}</april-first>
"""

#
# 3. Put your query for this problem between the triple quotes found below.
#
query3 = """
    for $o1 in //oscar
    for $o2 in //oscar
    let $pid := $o1/@person_id
    where $pid = $o2/@person_id
    and number($o1/year/text()) = number($o2/year/text()) - 1
    return <back-to-back> {<name>{//person[@id = $pid]/name/text()}</name>,
    <first-win>{$o1/type/text(), ' (', $o1/year/text(),')'}</first-win>,
    <second-win>{$o2/type/text(), ' (', $o2/year/text(),')'}</second-win>}
    </back-to-back>
"""

#
# 4. Put your query for this problem between the triple quotes found below.
#
query4 = """
    for $rting in distinct-values(//movie/rating)
    return <rating-info>{<rating>{$rting}</rating>,
        <num-movies>{count(//movie[rating=$rting])}</num-movies>,
        <avg-runtime>{avg(//movie[rating=$rting]/runtime)}</avg-runtime>,
        for $m in //movie
        where $m/rating = $rting
        and $m/earnings_rank <= 10
        return <top-ten>{ $m/name/text()}</top-ten>}
    </rating-info>
"""

#
# 5. Put your query for this problem between the triple quotes found below.
#    (only required of grad-credit students)
#
query5 = """
    for $p in //person
    let $direct := tokenize($p/@directed, '\s')
    let $act := tokenize($p/@actedIn, '\s')
    return if(count($act) >= 1 and count($direct) >= 1) then(
    <actor-director>{
    <name>{$p/name/text()}</name>,
    <num-acted>{count($act)}
    </num-acted>,
    <num-directed>{count($direct)}
    </num-directed>,
    for $m in //movie
    for $d1 in $direct
    for $a1 in $act
    where $m/@id = $d1
    and $a1 = $d1
    return <acted-and-directed>
    {$m/name/text()}
    </acted-and-directed>}
    </actor-director>)
"""

#
# 6. Put your query for this problem between the triple quotes found below.
#    (only required of grad-credit students)
#
query6 = """

"""
