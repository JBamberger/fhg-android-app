__First off, thanks for considering to contribute to the project.__

Things you can do: 
- reporting bugs
- submitting feature requests and ideas
- submit pull requests
- or just ask me


# Bug reports:

Create an issue with the bug report template and include as much information as possible. The more
information is available, the easier it is to fix the issue.

# Feature requests:

Describe what you would like to see in the Application using the feature request template. Then we
can negotiate if and how this can be done.

# Contributing code or updating the repository:

If you'd like to contribute code or update some other file in the repository you create a fork of
the repository. Then you make the desired changes in your copy of the repository and push them. If
you are happy with what you've done you can create a pull request. Describe your the changes you
made and why they are necessary. Then I'll take a look at it and merge the changes if appropriate.
If there are some further changes required, you can just add another commit in your copy and the PR
is updated automatically.


Some points about PRs:
- If you want to solve an issue leave a short comment at the issue so that others know
  about it and don't solve the same problem.
- Make sure, that the CI does not throw any errors
- Ideally add some test cases to the PR (Yes, i am aware that the test coverage is not great at the
  moment, but it is on the todo list :P).

# Deployment and releases:

There is no automation for deployment and releases because there was no need to do so. This might
change in the future but for now I'll do it by hand.

## Release checklist:

- Make sure the app builds cleanly and the CI has no errors.
- Bump the version code and version name.
- Build release bundle with upload certificate.
- Check if the release binary is not broken.
- Create a git tag describing the changes.
- Push the release to git.
- Update the Github release.
- Create a new release in Google Play.
- Upload the deobfuscation files if necessary.

# Questions

Just write a mail to me or add a comment on an issue if the question is related.
