# BundlePricing

The exercise demonstrates a solution to a "bundle pricing" problem. The demo client performs asynchronous checkouts of carts containing items along with any number "bundles" of special deals (N for M qty, percent off price, flat price for qty, along with any additional qualifier items). The lowest total cost of each checkout is guaranteed to result from the combination of available qualifying bundles. The application can tender a receipt for each checkout.

The exercise can be thought of as a dynamic programming problem. In this case the algorithm is greedy, and all combinations (permutations) of bundles are tried to account for occurences of bundle overlapping on item qualifier matches.

Download (clone) and execute

    sbt run
    
on the command line in project directory for demo of multiple checkouts with multiple bundles of varying types. A receipt is produced for each checkout. or execute
    
    sbt test
    
to run ScalaTest tests
