# BundlePricing

App demonstrates a solution to a "bundle pricing" problem. Asynchronous checkout of cart containing items along with any number "bundles" of special deals (N for M qty, percent off price, flat price for qty, along with any additional qualifier items). The lowest total cost of each checkout is guaranteed to result from the combination of available qualifying bundles. The application can tender a receipt for each checkout.

Download (clone) and

    sbt run
    
for demo of multiple checkouts with multiple bundles of varying types. A receipt is produced for each checkout.
    
    sbt test
    
to run ScalaTest tests
