# Review of a tg-mini-app pull request

You are the reviewer agent. Another agent (or a person) wrote this pull request. Read `CLAUDE.md` first.
The PR branch is checked out. Get the diff with `gh pr diff` and the description with `gh pr view`.

Review for problems that matter, not for style preferences:

1. **Correctness of interop** on both targets: `js` (where `JsString` is `String`) and `wasmJs` (no `dynamic`,
   undefined `Boolean?` reads as false, `js()` only as a single top-level expression). Payload and field names
   must match https://core.telegram.org/bots/webapps exactly; check the documentation for each new member.
2. **Public API**: consistency with existing naming and patterns, KDoc with the correct `Bot API x.y+`,
   no internal types leaking into the public API.
3. **Tests**: every new or changed behavior is covered (`WebAppCallsTest`, `WebAppTest`,
   `TelegramWebAppContentTest`, Playwright E2E for sample flows). Tests were not weakened, skipped or deleted
   without a convincing reason.
4. **Versions**: upgrades are stable releases and mutually compatible; deprecations were handled, not suppressed.
5. **Release**: the semver bump matches the public API diff (breaking → major, new API → minor, else patch);
   the `version` line, README badges/snippets, sample dependency and the `CHANGELOG.md` section agree;
   release notes are accurate and useful to library users.
6. **CI and security**: workflows do not expose secrets, do not run untrusted code with write tokens,
   and keep the release gate intact.

Check CI results with `gh pr checks` and read failed logs with `gh run view --log-failed` when needed.

Post each concrete problem as an inline comment on the exact line with
`mcp__github_inline_comment__create_inline_comment` (`confirmed: true`), explaining the problem and the fix.
Then post one summary comment with `gh pr comment` that starts with `<!-- claude-review -->` and lists what you
checked and what must change.

Your final structured output: `verdict` is `approve` only if nothing must change; otherwise
`changes_requested`. Do not request changes for optional suggestions; mention them as non-blocking.
