# GeneticSimulator (Work in Progress)

This is a personal project of mine in an attempt to create a terrarium of creatures that evolve over time.
This is developed using Java and no third party libraries are used.

### World

  The world is a randomly generated terrain made up of a grid of tiles.
  Each tile is colored using HSV colors where each channel represents the food type (hue), food count (value), and temperature (saturation).
  These three values affect what creatures are able to live on each tile as they move around.


### Creatures
  
  Each creature has multiple traits or features that can be evolved over time.
  Initially, each creature is created at random.
  The initial values for each trait or feature as well as the neurons in the brains are randomized.

#### Death Mechanics

  Each creature has an initial amount of *life*.
  Once that *life* value hits zero, the creature will die.
  The base amount of *life* is a set global value that gradually increases at the simulation progresses and a certain amount of *life* can also be inherited by the parents.
  The creatures has another value, *Rate of Death (RoD)*, that determines how quickly the amount of *Life* the creature has decreases.
  The *RoD* changes depending on how much energy the creature has, and if it is in combat with another creature.
  
  Once a creature dies, a portion of its energy is added to the food count of the tile it died on.
  
#### Trains and Features

  The main traits that are inherited and evolved is the food type, temperature tolerance, and size.
  The extra features that a creature may have are the number of feelers, the feeler lengths, and feeler angles.
  Each trait or feature can both have upsides and downsides.
  
  - **Food Type** -- More energy will be gained as the food type of the tile is closer to the food type of the creature. 
  - **Temperature Tolerance** -- The larger the difference between the temperature of the tile and the temperature tolerance of the creature, the more energy is consumed.
  - **Size** -- The larger the size of the creature, the more energy it can store, but also the more energy is consumed when moving.
 
  
#### Brain

  The brain of the creature is a neural network that evolves over the generations of creatures.
  The network architecture that is used is based on the *Neural Evolution of Augmenting Topologies (NEAT)* where the actual structure of the network can change.
  
  
  

