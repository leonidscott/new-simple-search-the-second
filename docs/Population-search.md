Population-Search: This is the main method of our algorithm. It takes in a mutation function, number of inividuals per generation, number of selected inividuals to select, the instance and max steps. We repeatedly make new generations by using top the top individuals and either mutating them or recombining them with other top and worst individuals. In these generations we score each individual and assign the highest scored individual as the `best`. 

Make-Next-Gen: This method is used to make each new generation from the selected individuals. We use the method `make-children` to make n children for the new generation. When we make children, we get them by recombining parents or mutating a parent. Child is muatated again after this by either adding or removing an item depending if it's overweight or not.

Two-Point-XO: 
In this method we take in a `mom-instance` and `dad-instance`. We use the count of mom's `:choices` as our `num-items`. This is used as a range to randomly generate our two points. The first point can be anywhere in the vector (except the very end), while the second point can be anywhere after the first point and before the end. From here, we hand variables to our `Combine-Two-Point-Choices` (which makes our new `:choices`) and then makes an instance of the result.

Combine-Two-Point-Choices: 
In this method we take in `mom-choices`, `dad-choices`, `point1`, and `point2`. We make the new vars `diff` (to know the difference between point1 and point2) and `end` (to know the distance from point2 to the end). With these vars, we return a new vector by concatinating sections taken from mom-choices (the beginning), then dad-choices (the middle), and finally taken from mom-choices again (the end).

Uniform-XO: 
In this method we take in a `mom-instance`and `dad-instance`. We use the count of mom's `:choices` as our `num-items` and make `per-vec` (a vector the same length as `:choices` but with randomly generated decimals between 0 and 1). We go through the per-vec and if the percent is larger than our set 50% we take and item from mom or else dad. This gives us an new mixed individual half from mom and half from dad.


