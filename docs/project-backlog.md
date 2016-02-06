#Product Backlog

##Problem Statement

* We would like to explore vast social media connections in order to find links between any two given people.

* We will do this by utilizing Twitter's vast number of public user connections to analyze how many steps apart two individuals are. Twitter’s API is what we will use to gather information from their database. The info will be processed in our algorithm to gather a set of connections.

##Background Information
* The problem is based on the concept that every person is connected to every other by 6 mutual knowings (or degrees). The targeted users are anyone who has a Twitter account and wishes to know how they are related to another user.
* Similar applications are “The Oracle of Bacon” which shows how Kevin Bacon is related to any actor on IMDB. Facebook had a Six Degrees application that was produced in a 2010 London Hackathon. 
* One of the limitations to “The Oracle of Bacon” is that it only shows connections relating to Kevin Bacon. We will address this issue by broadening the connections to all of Twitter. Facebook’s Six Degrees app is currently taken down and had restrictions based on user privacy. Our app will have no restrictions on users as long as they exist on Twitter.

##Functional Requirements

####As a user I would like to...
1. be able to view band connections on last.fm
1. be able to view article connections on Wikipedia.
1. select the site I would like to view connections on.
1. see how I am related to a node of my choice.
1. see how I am related to a node.
1. see how two nodes of my choice are connected.
1. see how two random nodes are connected.
1. have a drop down menu appear to try and autocomplete my query.
1. view the connection chain.
1. click on nodes in my connection chain.
1. view more information about each node in the chain.
1. share my connection chain on social media.
1. export my connection chain to my personal computer.
1. be notified of any crashes, bugs, or problems that the program might run into as it is trying to find a connection.
1. be prompted to continue if computation is taking longer than expected.
1. have the option to stop computation if they don't want to wait any longer.
1. be entertained while I have to wait for the result.
1. be able to read about the algorithm being used and the project in general.
1. see how many times the algorithm has run for each site.
1. view searches I have recently made.
1. being able to click on recently made searches to go back to them.
1. view what other users have been searching.
1. be able to view friend connections on Twitter (time permitting).
1. be able to view related actors on IMDb (time permitting).
1. be able to view friend connections on Steam (time permitting).

####As a back-end developer I would like to...
1. keep a record of people that have used the app.
1. keep a record of searches users have made.
1. create an algorithm that finds how nodes are connected.
1. keep the algorithm generic so it may be used in the future with other sites.
1. view the number of API requests a user has made, and discretely display this information on the site.

####As a front-end developer I would like to...
1. create a minimalistic design for the site.
1. create a way to support the various methods of inputting who will be connected.

##Non-functional Requirements

####As a user I would like...
1. that the app responds quickly (in less than 10 seconds of my query).
1. be able to intuitively understand website layout.

####As a developer I would like...
1. that the version control is well organized with with a release branch, a development branch, and a branch for each feature that gets merged into the development branch upon completion.
1. that release versions are tagged in version control.
1. to have the user stories well organized and easily viewable.
1. that the sprints be well planned out and manageable.
1. to easily communicate within the group.
1. create heuristics for the algorithm.
1. create a caching system for searches.
1. create a caching system for connections.
1. make the algorithm use as few API requests as possible.
1. be able to handle multiple requests at the same time.
1. be able to deal with users trying to trick us (entering same name twice, people that don't exists).
