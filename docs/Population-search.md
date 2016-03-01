Two-Point-XO: 
In this method we take in a `mom-instance` and `dad-instance`. We use the count of mom's `:choices` as our `num-items`. This is used as a range to randomly generate our two points. The first point can be anywhere in the vector (except the very end), while the second point can be anywhere after the first point and before the end. From here, we hand variables to our `Combine-Two-Point-Choices` (which makes our new `:choices`) and then makes an instance of the result.

Combine-Two-Point-Choices: 
In this method we take in `mom-choices`, `dad-choices`, `point1`, and `point2`. We make the new vars `diff` (to know the difference between point1 and point2) and `end` (to know the distance from point2 to the end). With these vars, we return a new vector by concatinating sections taken from mom-choices (the beginning), then dad-choices (the middle), and finally taken from mom-choices again (the end).

Uniform-XO: 
In this method we take in a `mom-instance`, `dad-instance`, and `percent`. We use the count of mom's `:choices` as our `num-items` and make `per-vec` (a vector the same length as `:choices` but with randomly generated decimals between 0 and 1). Like `Two-Point-XO`, we hand variables to our `Combine-Uniform-Choices` (which makes our new `:choices`) and then makes an instance of the result.

Combine-Uniform-Choices: 
In this method we take in `mom-choices`, `dad-choices`, `per-vec` and `percent`. We first define our var `index` to be 0. With these vars, we return a new vector by concatinating items repeatedly taken from mom-choices and dad-choices. We get these items from our `Get-Parent` method.

Get-Parent: 
This method takes in `mom-choices`, `dad-choices`, `per-vec`, `percent`, and `index`. We first re-define our var `index` to be incremented by one, this is so each time the function is called within `Combine-Uniform-Choices` our index allows us to move forward in the vectors. Once we have the appropriate index, we check the percent at index of per-vec and compare it to our given percent. If it's less than the given one, we return this item from mom-choices. Otherwise we return this item from dad-choices.
