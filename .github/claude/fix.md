# Address review feedback on a tg-mini-app pull request

You are the maintenance agent. The reviewer agent requested changes on this pull request. Read `CLAUDE.md` first.
The PR branch is checked out.

1. Read the latest summary comment starting with `<!-- claude-review -->` (`gh pr view --comments`) and all
   unresolved review threads (`gh api graphql` on `pullRequest.reviewThreads`).
2. Fix every blocking problem. If you disagree with a comment, do not change the code; reply in the thread
   with a clear technical justification instead.
3. Run `bash scripts/ci/verify.sh` (set `E2E_INSTALL_DEPS=1`); everything must pass. Never weaken tests.
4. If the fixes change the public API or the release, update the version bump and `CHANGELOG.md` accordingly.
5. Commit with a message starting with `review-fix:` and push to the PR branch.
6. Reply in each thread with what you changed (or why not), and resolve the threads you fixed with the
   `resolveReviewThread` GraphQL mutation.
