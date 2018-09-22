__First off, thanks for considering to contribute to the project.__

Things you can do: 
- reporting bugs
- submitting feature requests and ideas
- submit pull requests
- or just ask me what to do ;)


# Bug reports:

Create an issue with the bug report template and include as much information as possible.
The more information is available, the easier it is to fix the issue.

# Feature requests:

Create a detailed description, using the feature request template.
I will then give my opinion on the topic and then we can decide what to do with the idea.



# The development:

In the issues view you can also see the milestones of the application.
each milestone, consists of a major.minor version number and has a corresponding development branch called dev-major.minor
If you want to fix an issue for some milestone, fork the repository and create a branch dev-major.minor-#issue
After you finished the development, create a pull request from your branch to the dev-major.minor branch.

There is only one exception. The currently deployed version has no dev-major.minor branch, since the code is on the master branch.
If there are some issues for the current version, there should be a branch called dev-major.minor.patch.
This branch should only contain bug fixes and no functional changes.
For an issue in the deployed app, create a branch called dev-major.minor.patch-#issue and create a pull request against the patch branch.

Some points about PRs:
Make sure, that travis thrown no errors and include test cases if possible 
(Yes, i am aware, that the test coverage is not great at the moment, but it is on the todo list :P).

# Deployment

At the moment, i will do all the deployment work. Maybe i'll automate this at some point, but at the moment there is no need to do so.

# Questions

Just write a mail. You can do so from the application if you do not want to search my address.
