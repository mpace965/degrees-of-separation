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
1. be able to sign in with my Twitter account.
2. see how I am related to a Twitter user of my choice.
3. see how I am related to a random Twitter user.
4. see how two Twitter users of my choice are connected.
5. see how two random Twitter users are connected.
6. have a drop down menu appear to try and autocomplete my query.
7. view the connection chain easily and beautifully.
8. click on people in my connection chain and view their Twitter page
9. view the connection in a reasonable amount of time.
10. be notified if I must wait if Twitter rate limits my requests.
11. share my connection chain on social media.
12. export my connection chain to my personal computer.
13. be able to intuitively understand website layout.
14. be notified of any crashes, bugs, or problems that the program might run into as it is trying to find a connection.
15. be prompted to continue if computation is taking longer than expected.
16. have the option to stop computation if they don't want to wait any longer.
17. be entertained while I have to wait for the result.
18. have the option to follow the person that I am connected with and everyone else in the chain.

####As a back-end developer I would like to...
1. keep a record of people that have used the app.
2. have Twitter not limit how much we access their API.
3. create an algorithm that finds how two users are connected.
4. make the algorithm as efficient as possible.
5. make the algorithm use as few Twitter API requests as possible.
6. keep the algorithm generic so it may be used in the future with other social media sites.
7. have the database stored on Amazon Web Services.
8. have the program not crash and always compile.
9. be able to handle multiple requests at the same time.
10. be able to deal with users trying to trick us (entering same name twice, people that don't exist, friendless Twitter accounts).

####As a front-end developer I would like to…
1. use React.js to make a usable interface.
2. use d3.js to create the connection chain.
3. create a minimalistic design for the site.
4. keep the user entertained while they wait.
5. create a way to support the various methods of inputting who will be connected.
6. use Chrome’s development tools to debug and maintain the JavaScript.

##Non-functional Requirements

####As a user I would like...
1. that the app responds quickly (in less than 10 seconds of my query).
2. to not have my Twitter information leaked or stolen (without my permission).

####As a developer I would like...
1. that the version control is well organized with with a release branch, a development branch, and a branch for each feature that gets merged into the development branch upon completion.
2. that release versions are tagged in version control.
3. to have the user stories well organized and easily viewable.
4. that the sprints be well planned out and manageable.
5. to easily communicate within the group.
