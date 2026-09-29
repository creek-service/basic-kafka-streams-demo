# Plan: Ship Creek `0.5.0` with JSON payload support

Goal: JSON payload support, demonstrated and documented across every Creek demo repo, shipped as a
coordinated `0.5.0` release. This doc tracks what's outstanding; historical investigation detail has
been trimmed — see git history of this file if it's ever needed.

## Status

| Repo | Role | PR | CI |
|---|---|---|---|
| `aggregate-template` | Bootstrap template every repo below is created from | [#1004](https://github.com/creek-service/aggregate-template/pull/1004) | Local build/system tests pass; new PR checks running after `d582649`; review comments addressed (Outstanding #1) |
| `basic-kafka-streams-demo` | Tutorial 1 | [#758](https://github.com/creek-service/basic-kafka-streams-demo/pull/758) | Local build/system tests pass; new PR checks running after `2ee0ac4` |
| `ks-connected-services-demo` | Tutorial 2 | [#686](https://github.com/creek-service/ks-connected-services-demo/pull/686) | Local build/system tests pass; new PR checks running after `0adb6ea` |
| `ks-aggregate-api-demo` | Tutorial 3 | [#644](https://github.com/creek-service/ks-aggregate-api-demo/pull/644) | Local build/system tests pass; new PR checks running after `e8665f3` |
| `wip-state-stores-demo` | State-stores demo (WIP) | [#190](https://github.com/creek-service/wip-state-stores-demo/pull/190) | Local build/system tests pass; new PR checks running after `88dce3e` |
| `connected-services-demo` | Older 2-service demo, looks superseded by `ks-connected-services-demo` | — | Ask maintainer: archive instead of updating? |
| `creek-service.github.io` | Main docs site | — | Not started (Outstanding #6) |

All five code PRs target `0.4.5-SNAPSHOT`, are drafts, and are blocked on the real `0.5.0` release.

`aggregate-template`'s `TopicDescriptors` took on a different (better) design than the four demo
repos: `inputTopic`/`internalTopic`/`creatableInternalTopic`/`outputTopic` now **default to a JSON
value + Kafka-native key** (an explicit-format overload exists for anything else), rather than the
demo repos' approach of adding separate `inputTopicWithJsonValue`/`outputTopicWithJsonValue` methods
alongside natively-defaulting originals. The demo repos need migrating to match (Outstanding #3).

---

## Outstanding work, roughly in order

### 1. Address review comments on `aggregate-template#1004` — DONE

Completed on the `add-json-payload-support` branch (commits `4edd57f`, `2a8510a`,
`def7560`): comments/Javadoc shortened or removed, JitPack removed, repository order fixed,
review questions answered, and all review threads resolved. The bootstrap/add-service script
sequence was checked and `add_service.sh` now formats generated code to remove stale imports.
CI passed after `def7560` (including build and test-scripts); the non-Docker local build passed.
Docker Desktop subsequently recovered, allowing full Docker tests in all four demo repos.

Historical review checklist (completed):

The maintainer left inline review comments, now actioned. Fetch fresh with:
```
gh api repos/creek-service/aggregate-template/pulls/1004/comments --paginate
```
As last reviewed, the asks were:
- **General**: reduce/remove comments added by the PR; make any surviving Javadoc succinct; remove
  Javadoc from private types/methods/fields entirely (`TopicDescriptors.java` is the main offender —
  the class-level Javadoc, and Javadoc on the private `KeyValueDescriptor`/`BaseJsonSchema`/etc.
  inner classes, all need trimming or removing).
- `api/build.gradle.kts`: several two-line "remove if not using JSON" comments should collapse to
  one line each (e.g. combine the separate comments above `creek-base-annotation` and
  `jackson-annotations` into a single "Remove both if not using JSON payloads:" above them both).
- `ExampleAggregateDescriptor.java` / `ExampleServiceDescriptor.java`: drop the extra inline
  explanation on the `KAFKA_FORMAT` lines (e.g. `// native, not JSON: String has no schema`) — keep
  just the `// init:remove` marker, matching the surrounding lines' style.
- `common-convention.gradle.kts`:
  - Remove the changelog entry added to the file's version-history docstring (`- 1.14: ...`) — it's
    a temporary change that won't be merged as-is, so shouldn't get a permanent changelog line.
  - Reorder `repositories {}` so `mavenLocal()` is first.
  - **"Is the JitPack repo needed?"** — Investigated: `./gradlew :example-service:dependencies
    --configuration runtimeClasspath --refresh-dependencies` resolves cleanly without it, and
    `everit-json-schema` still appears in the dependency tree at `0.4.5-SNAPSHOT` as
    `com.github.erosb:everit-json-schema:1.14.6`, which resolves from Maven Central, not JitPack.
  - **"Is `creek.schema.json { typeScanning.moduleWhiteList(moduleName); ... }` needed, or is it the
    default?"** — Investigated via `creek-json-schema-gradle-plugin` source
    (`JsonSchemaPlugin.java`): the plugin's own convention default is `ALL_MODULES` (an *empty*
    whitelist, meaning unrestricted/scan-everything). Calling `.moduleWhiteList(moduleName)`
    genuinely narrows scanning to just this module, which is **not** the default and is worth
    keeping (avoids scanning the whole classpath). Replied on the PR thread; no code change needed.

### 2. Migrate the four demo repos to the new `TopicDescriptors` API — DONE, PUSHED

Implemented, verified and pushed to the existing draft PRs (2026-09-29):

| Demo | Helper replaced | Call sites / tests | Descriptor docs | Build / docs / system tests |
|---|---|---|---|---|
| `basic-kafka-streams-demo` [#758](https://github.com/creek-service/basic-kafka-streams-demo/pull/758) | Done; two SpotBugs suppressions needed for private constructors | Done; native test formats explicit | Updated `04-service-descriptor.md` | `./gradlew build systemTest` + Jekyll build pass; `a0d3c21` |
| `ks-connected-services-demo` [#686](https://github.com/creek-service/ks-connected-services-demo/pull/686) | Done; same two SpotBugs suppressions | Done; `twitter.tweet.text` explicitly native | Updated `04-service-descriptor.md` | `./gradlew build systemTest` + Jekyll build pass; `ed8b487` |
| `ks-aggregate-api-demo` [#644](https://github.com/creek-service/ks-aggregate-api-demo/pull/644) | Done; same two SpotBugs suppressions | Done; external ingestion topic explicitly native | Updated `04-creek-aggregate-descriptor.md` | `./gradlew build systemTest` + Jekyll build pass; `afe2e45` |
| `wip-state-stores-demo` [#190](https://github.com/creek-service/wip-state-stores-demo/pull/190) | Done; no suppressions needed | Done; native test formats explicit | No "Define topic resources" page in this WIP tutorial | `./gradlew build systemTest` + Jekyll build pass; `1de86be` |

Each demo repo previously had its own `TopicDescriptors.java` with separate
`inputTopicWithJsonValue`/`outputTopicWithJsonValue` methods (added before `aggregate-template`'s
default-to-JSON design was settled). For each of `basic-kafka-streams-demo`,
`ks-connected-services-demo`, `ks-aggregate-api-demo`, `wip-state-stores-demo`:

1. Replace the repo's `TopicDescriptors.java` with the `aggregate-template` PR branch's version,
   adjusting the package name (plus SpotBugs suppressions where needed).
2. Update every call site: `inputTopicWithJsonValue(...)`/`outputTopicWithJsonValue(...)` →
   `inputTopic(...)`/`outputTopic(...)` (now equivalent, JSON is the default). Where a key or value
   is deliberately *not* JSON, use the explicit-format overload instead.
3. Update each repo's "Define the topic resources" doc page to describe `inputTopic`/`outputTopic`
   as JSON-by-default, rather than presenting the `...WithJsonValue` methods as how you opt in.
4. Re-run `./gradlew build`/`./gradlew systemTest` and the docs build.

In three repos the SpotBugs suppressions differ from the template copy by an annotation import
and two constructor annotations per repo. `CT_CONSTRUCTOR_THROW` was reported on the private
`TopicDescriptor` and `BaseJsonSchema` constructors by those repos' SpotBugs configurations.

The first basic-demo Docker run failed because `build/docker` retained older Jackson `2.22.1`
jars after Gradle's distribution had moved to `2.22.3`. `prepareDocker`'s Copy task does not
remove obsolete files from its output directory. Moved the old generated Docker context to an
approved temp directory (preserved it), reran `build systemTest`, and it passed. Rebuilt the
other three repos from fresh Docker contexts as well; all pass. This is a build-output hygiene
issue, not a topic-descriptor failure. The four code commits were pushed to their existing PR
heads; the basic repo's pre-existing staged `plans/add-json-payload-support.md` state was left untouched.

### 3. Schema fidelity gaps — DONE, PUSHED

For each of `basic-kafka-streams-demo`, `ks-connected-services-demo`, `ks-aggregate-api-demo`,
`wip-state-stores-demo`:

Generated JSON schemas didn't previously enforce the same constraints as the annotated Java POJO/record's
constructors (e.g. a `String` field's non-empty check, an `int` field's `> 0` check), because Jackson
only treats primitives as implicitly required.

Fix applied to all four repos:
- Mark non-`Optional<>` String fields/constructor parameters `@JsonProperty(required = true)` (already present
  for `handle` in `ks-connected-services-demo`; added elsewhere). All four repos' compact constructors already
  null/empty-checked these fields, so no constructor changes were needed.
- Add `io.swagger.core.v3:swagger-annotations` and annotate with `@Schema(minLength = 1)` (non-empty strings) /
  `@Schema(minimum = "1")` (positive counts), then regenerate schemas (`./gradlew :api:generateJsonSchema`) and
  confirm `required`/`minLength`/`minimum` now appear as expected.

Non-obvious wrinkle hit in all four repos: `creek-kafka-json-serde`'s Confluent schema-registry client
transitively pulls in `io.swagger.core.v3:swagger-annotations-jakarta`, which claims the same JPMS module
name (`io.swagger.v3.oas.annotations`) as the plain `swagger-annotations` artifact. Declaring the new
dependency as `api`/`implementation` therefore broke Docker-image startup with a `FindException: Two
versions of module io.swagger.v3.oas.annotations found`. Fixed by declaring it `compileOnlyApi` (keeps it off
the runtime/Docker classpath, but still visible on downstream compile classpaths so consumers don't get
"class file for ... Schema not found" `-Werror` failures) plus a `testCompileOnly` entry (the module
descriptor's `requires static` needs it resolvable when compiling the test module patch) and `requires static
io.swagger.v3.oas.annotations;` in `module-info.java`.

Also cleared each repo's stale `<service>/build/docker` directory before the first `build systemTest` run of
this session (same `prepareDocker` Copy-task hygiene issue noted in step 2 — it doesn't prune obsolete jars),
moving old contents to an approved temp dir rather than deleting.

Docs updated where they described the gap this step fixes: `basic-kafka-streams-demo`'s
`04-service-descriptor.md` (corrected the "schemas do not yet require..." note) and
`ks-aggregate-api-demo`'s `04-creek-aggregate-descriptor.md` "A word about dependencies" section (which
enumerated the `api` module's production dependencies — updated to describe the new compile-only one).
`ks-connected-services-demo` and `wip-state-stores-demo` had no equivalent stale doc text.

Progress:

| Demo | Status |
|---|---|
| `basic-kafka-streams-demo` | DONE, PUSHED — `0a75a34`; `./gradlew build systemTest` + Jekyll build pass |
| `ks-connected-services-demo` | DONE, PUSHED — `e2307d3` (on `pr_/quirky-franklin-951rpq`); `./gradlew build systemTest` + Jekyll build pass |
| `ks-aggregate-api-demo` | DONE, PUSHED — `2b6a3b8` (on `pr_/wonderful-rubin-97rrz3`, rebased on a concurrent Codecov-fix commit `3cc0a56`); `./gradlew build systemTest` + Jekyll build pass |
| `wip-state-stores-demo` | DONE, PUSHED — `348ae75` (on `pr_/quirky-meitner-l0649g`); `./gradlew build systemTest` pass; docs build pass (pre-existing unrelated Sass deprecation warnings only) |

### 4. Reproducibility check: does following the tutorial from a fresh repo actually produce the demo repo?

For `basic-kafka-streams-demo`, start directly from `aggregate-template`. The other three tutorials
explicitly start from a **completed basic demo**, so first reproduce that demo and then fork the
result into each subsequent tutorial (with the destination repo name); do not compare a bare
aggregate-template bootstrap against a later-stage tutorial.

For each of `basic-kafka-streams-demo`, `ks-connected-services-demo`, `ks-aggregate-api-demo`,
`wip-state-stores-demo`:

1. Simulate bootstrapping the basic demo from the template **by running the template's scripts
   directly from the `aggregate-template` PR branch** (`add-json-payload-support`) — there's no real
   GitHub org to click "Use this template" against, so this replaces that step and the bootstrap
   GitHub Action. For later demos, instead start with the finished basic-demo reproduction and
   follow that tutorial's own bootstrap instructions:
   ```
   cp -R <local aggregate-template checkout, on the add-json-payload-support branch> /tmp/repro-<demo>
   cd /tmp/repro-<demo>
   git checkout add-json-payload-support   # if not already
   ./.creek/bootstrap.sh "<org>/<repo-name>" "<org>"
   ./.creek/clean_up.sh
   ./.creek/add_service.sh <first service name, e.g. handle-occurrence-service>
   # ...and any further add_service.sh calls the demo actually has (e.g. a 2nd service for
   # ks-connected-services-demo / wip-state-stores-demo)
   ```
2. Follow that demo's own published tutorial steps by hand against this fresh repo (use the demo's
   site, rendered via `cd docs && bundle exec jekyll serve --livereload --baseurl
   /<repo-name>`) — write the model classes, service descriptor,
   topology, tests, and system-test fixtures exactly as instructed.
3. Ensure following the instructions results in code with a green build, running `./gradlew format`, then `./gradlew`.
   If it does not, workout if the issue is in the template repo or the demo's instructions and fix.
   If a fix is needed, clean down and run this stage again to ensure it now works.
4. Diff the result against the real demo repo (same branch/PR, e.g. `json-wip` for
   `basic-kafka-streams-demo`). **They should be the same** — the whole point is that a reader
   following the docs from scratch ends up with what's actually in the repo.
5. Where they differ, the default assumption is **the demo repo is wrong / stale and should be
   updated to match** what the tutorial actually produces — not the other way around. (If a
   difference instead reveals the *tutorial text* is wrong or ambiguous, fix the docs, then repeat
   step 2 for that section.)
6. Re-run `./gradlew build`/`./gradlew systemTest` and the docs-site build for the demo repo after
   any fix.

The `...WithJsonValue` naming mismatch was addressed in step 2 for the real demo repos; the
fresh-from-template service descriptor already calls plain `inputTopic`/`outputTopic`.

Progress (2026-09-29):

| Demo | Fresh bootstrap | Follow tutorial / diff | Fixes / verification |
|---|---|---|---|
| `basic-kafka-streams-demo` | Fresh corrected-template bootstrap → clean_up → add_service passes at `/private/var/folders/_t/0frj1b2s3k974hxrlns3hgsc0000gn/T/opencode/repro-package-fix` | Applied tutorial's models, descriptor, topology, unit tests, YAML fixtures, and API dependencies; reader-authored code/fixtures match the real demo; unrelated generated-scaffolding drift remains | Reproduction `./gradlew format build` (includes Docker system test) passes; real demo build/system tests and Jekyll previously passed |
| `ks-connected-services-demo` | Forked completed basic reproduction and ran basic-demo `bootstrap.sh`, `clean_up.sh`, `add_service.sh handle-occurrence-filtering-service` at `/private/var/folders/_t/0frj1b2s3k974hxrlns3hgsc0000gn/T/opencode/repro-connected-json` | Applied new service model/descriptor/topology/test and system-test fixtures; corrected old native-String first-service drift in real demo; main tutorial-authored files and fixtures now match; inherited scaffolding drift remains | Reproduction and real repo both pass `./gradlew format build systemTest` (Docker); real docs Jekyll build passes; pushed `9c87d28` |
| `ks-aggregate-api-demo` | Forked completed basic reproduction; ran basic-demo bootstrap/clean-up at `/private/var/folders/_t/0frj1b2s3k974hxrlns3hgsc0000gn/T/opencode/repro-aggregate-json` | Retained inherited `TweetData`/`HandleUsage` JSON and first-service topology/fixtures; moved topic ownership to aggregate descriptors, registered external descriptor, updated unit-test schema mock; core tutorial files match real demo, with unrelated scaffolding drift | Both fork and real repo pass `./gradlew format build` (real repo also explicit `systemTest`); real Jekyll build passes; pushed `5bd3e45` to PR #644 |
| `wip-state-stores-demo` | Forked completed basic reproduction; ran basic-demo bootstrap/clean-up and `add_service.sh handle-scoreboard-service` at `/private/var/folders/_t/0frj1b2s3k974hxrlns3hgsc0000gn/T/opencode/repro-state-json` | Realigned checked-in code/template to generated `wip.state.stores.demo` package and inherited basic service. The WIP add-service page now instructs moving seed tweets to ordinary inputs, because this repo's expectation observer missed four seed-derived records; no state-store lesson exists yet | Fresh checkout and aligned real repo pass Docker-backed builds after fixture migration; real docs Jekyll build passes; pushed `2fd31de` to PR #190 |

Package-alignment fixes are **committed and pushed** to all five PR branches (2026-09-29):
`aggregate-template` `d582649`, `basic-kafka-streams-demo` `2ee0ac4`,
`ks-connected-services-demo` `0adb6ea`, `ks-aggregate-api-demo` `e8665f3`, and
`wip-state-stores-demo` `88dce3e`. The template's topology tests now share the production
topology package, so `add_service.sh` renames both without a new script branch. The real demos'
first-service production and test packages match what the script generates; second-service tests
in the connected-services and state-stores demos, plus the retained `.creek/service_template`
test sources, were aligned. The basic tutorial no longer asks the reader to rename packages.
`./gradlew format build systemTest` passed locally in all five repos; the Jekyll docs build
passed in the four demos (the template's docs were not rebuilt in this pass).
For the template, aggregate-API demo and state-stores demo, initial Docker runs encountered
obsolete `commons-validator` jars left in `build/docker` by Gradle's Copy task; the old contexts
were preserved in the approved temporary directory and the clean-context reruns passed. At the
the latest GitHub check, all five PRs' build/analysis/pages/script checks passed (2026-09-29).
`aggregate-template#1004` showed `BEHIND` earlier; that was not a CI failure.

Use a disposable template checkout for the basic demo, then fork its completed reproduction for
the later demos; keep actual demo checkouts and uncommitted changes intact. Record differences
and the exact verification performed here.

#### `basic-kafka-streams-demo`: findings and fixes

The corrected fresh replay now passes the full build, including the seed-data Docker system test.
Its model records, service descriptor, topology, unit test and YAML fixtures match the demo
exactly. Many remaining files differ because the checked-in demo retains older generated
scaffolding and plugin versions; distinguish that drift from tutorial-authored differences when
comparing. In particular, the template uses a different SpotBugs version from the demos, so its
`TopicDescriptors` does not need the same constructor suppressions.

When using the completed basic demo as a template for later tutorials, its *own*
`.creek/bootstrap.sh` also changes the aggregate descriptor test's public class name without
renaming the test file. The same fix already made in `aggregate-template` is now applied to the
basic-demo bootstrap script; fresh connected-services bootstrap verified it. This basic-demo
script change was pushed as `6652a09` (with these status notes).

Initial finding: the tutorial's descriptor page still said `inputTopicWithJsonValue`/
`outputTopicWithJsonValue`, but the template's `TopicDescriptors` now defaults `inputTopic`/
`outputTopic` to JSON values — this was stale prose left over from step 2/3's code changes; the
descriptor doc has since been corrected as part of this step.

Template findings:

1. **`aggregate-template`'s `bootstrap.sh` bug (fixed, pushed `4cd2d05`).** It renames
   `ExampleAggregateDescriptor.java` to the aggregate's class name, and its global text-replace
   happens to also rename the class *inside* `ExampleAggregateDescriptorTest.java` — but never
   renames that test *file*, leaving a filename/class-name mismatch that fails to compile
   (`class BasicKafkaStreamsDemoAggregateDescriptorTest` inside a file still called
   `ExampleAggregateDescriptorTest.java`). This would hit anyone bootstrapping a new aggregate
   from the template, independent of JSON support. Fixed by also renaming the test file.
2. `add_service.sh` previously left tests in `...example.streams` because the template put them
   outside the production `...example.service.kafka.streams` package that the script renames.
    This wasn't build-breaking, but produced misleading test packages. The template test files
    have now been moved alongside `TopologyBuilder` (pushed `d582649`); the existing script
    renames both together. A fresh bootstrap/clean-up/add-service run verified the generated
    production and test paths match without manual moves.
   The four demo repos' retained service templates and the already-generated second-service tests
   were aligned too. Builds and Docker system tests pass in all five real checkouts.

The previous docs fix `84e810e` identified two apparent gaps, but the first was a mistaken
conclusion about which side should change:

1. The demo code, not the tutorial, was wrong to use `...demo.service` when `add_service.sh`
   produces `...demo.handle.occurrence.service`. The recently added manual package-rename section
   in `03-add-service.md` has been removed; production classes, tests, module declarations,
   launcher/build configuration, and snippet paths now use the generated package without any
    reader action (pushed `2ee0ac4`; corresponding changes pushed to the other three demos).
2. `04-service-descriptor.md` documented the `@JsonProperty`/`@Schema` annotations (step 3's
   schema-fidelity fix) but never explained the `api/build.gradle.kts` dependency additions
   (`jackson-annotations`, `swagger-annotations`, `spotbugs-annotations`), the matching
   `module-info.java` `requires static io.swagger.v3.oas.annotations;`, or the new
   `swaggerAnnotationsVersion` `gradle.properties` entry needed to compile them. Added an "A word
   about dependencies" section mirroring `ks-aggregate-api-demo`'s existing page, with a
   `begin-snippet`/`end-snippet`-wrapped `dependencies` block added to `api/build.gradle.kts` so
    the doc can quote it directly.

The earlier reproduction incorporated a rejected manual package rename. The corrected fresh
reproduction now builds and passes tests; remaining differences are JSON-unrelated drift and cosmetics:
- Copyright years and other pure template-version drift (Docker base image tag, Gradle wrapper
  version, `spotbugs`/`spotless`/`moduleplugin` plugin versions, axion-release plugin version,
  GitHub Actions workflow content, checkstyle config) — expected, since the reproduction was
  bootstrapped "today" against a repo whose scaffolding was generated at various earlier points;
  the "Tips" section below already covers this class of drift. Fixed by refreshing the
  reproduction's `buildSrc/` from the real repo once, after diagnosing that a stale
  `spotbugs-gradle-plugin` version (6.4.8 vs current 6.5.11) changed whether SpotBugs'
  `CT_CONSTRUCTOR_THROW` detector fires on `TopicDescriptors`' private constructors, in turn
  making the already-necessary `@SuppressFBWarnings` on them look like a *useless* suppression
  (itself a SpotBugs violation) — a build-breaking false-positive purely from an out-of-date
  plugin pin, not a real defect.
- Missing per-module `README.md` files (e.g. `handle-occurrence-service/README.md`) and a design
  diagram — hand-authored repo polish, not produced by the template or taught by the tutorial.
- A couple of stray empty directories and trailing-newline/`.DS_Store` noise in the real repo.

The four real demos, their service templates, and `aggregate-template` now follow the agreed
package convention. The corrected basic replay confirms the reader-authored files match.

#### `ks-connected-services-demo`: findings and fixes

The checked-in connected-services demo predated the completed JSON-enabled basic tutorial:
its first service declared `twitter.tweet.text` as `Long`/native `String` and its fixtures sent
scalar values. A fork of the actual basic tutorial instead retains `Long`/JSON `TweetData`;
feeding the old fixtures into that service failed deserialization. The demo now retains the
basic model, descriptor and topology, uses `TweetData` in its unit test, and uses JSON objects in
the system-test inputs. `04-service-descriptor.md` was corrected where it claimed the previous
tutorial used primitive values. The connected tutorial's own president-tweet inputs also use
`TweetData` objects.

The inherited basic test suite seeded two tweets before the service started. With both services
in the connected test suite, those seed-derived *output* records were not observed by the
expectation check (4 expected records missing, twice reproducible); putting the same records in
regular input made the combined test pass. The connected suite now moves those two tweets into
`inputs/twitter.tweet.text.yml` and removes the seed file, with an explicit tutorial instruction.
The basic demo still covers the original seed-data scenario. The exact two-service observation
behavior is not yet explained at the Creek system-test implementation level; investigate in #709
if maintaining seed coverage in the connected suite becomes a requirement. The filtering
service unit-test docs also now give the actual permissive mock schema-store setup (the old
`JsonSerdeExtensionOptions.testBuilder()` example could not resolve its unowned input schema)
and the expected topology instead of suggesting disabling the test. A newly generated
`TestTopics` helper is retained in the service and template. Connected-demo code/docs/fixtures
were pushed as `9c87d28`; basic-demo bootstrap fix was pushed as `6652a09`.

#### Remaining tutorial blockers

- `ks-aggregate-api-demo` (resolved in `5bd3e45`): The tutorial previously described primitive
  payloads, `UsageCount`, and native-`String` input despite starting from JSON-enabled basic.
  Preserved inherited JSON records and fixtures instead; descriptor ownership is now the only
  change taught by the tutorial. A service-only unit test needs a permissive schema-store mock
  after aggregate ownership changes. The fork and real repo both pass Docker system tests.
- `wip-state-stores-demo`: Only intro/bootstrap/add-service pages are published; the checked-in
  second service remains a generated shell, not a state-store implementation. The repo was using
  `io.github.creek.service.connected.services.demo`, whereas its bootstrap generates
  `io.github.creek.service.wip.state.stores.demo`; production/test/template packages and module
  names have now been realigned in `2fd31de`. The inherited basic test's two seed tweets initially
  produced four missing expected outputs in this repo (despite the basic fork passing): the
  expectation observer can start after seed records have already been processed. As in the
  connected demo, the two records now live in regular inputs, explicitly taught on the WIP
  add-service page. The published WIP portion matches the reproduced starting point plus this
  documented fixture migration, but no state-store lesson exists to verify; completing that
  separate tutorial needs its missing pages and implementation.

### 5. Doc site review:

For each of `basic-kafka-streams-demo`, `ks-connected-services-demo`, `ks-aggregate-api-demo`,
`wip-state-stores-demo`:

Review the doc site and highlight any gaps, bad instructions or improvements to wording.

### 6. (Lower priority, not blocking) `creek-system-test` resource-ordering hardening

[creek-system-test#709](https://github.com/creek-service/creek-system-test/issues/709) ("Seed data
is injected AFTER services-under-test start") describes the ordering problem this whole effort
depends on. It's already fixed well enough for every demo's own system tests to pass (verified this
cycle, including the seed-data-into-an-owned-topic case) — but the fix hasn't been reviewed for
whether it generalises to trickier scenarios (multiple services each owning schema'd topics; a
service that both produces *and* consumes schema'd topics). Worth a hardening pass before `0.5.0`,
but doesn't block any of the PRs above.

### 7. Cut the coordinated `0.5.0` release (core libraries)

Once the above is done, cut the 0.5.0 release. Ask the user to initite this.

### 8. Flip all PRs from SNAPSHOT to the real release, merge

For each of the five PRs:
- Bump `creekVersion` (and the JSON schema plugin version) from `0.4.5-SNAPSHOT` to `0.5.0`.
- Remove the temporary SNAPSHOT-only repository added in the PRs.
- Re-run `./gradlew`/` against the real release.
- Merge `aggregate-template` first, then the four demo repos.

### 9. Update the main docs site (`creek-service.github.io`) & announce

Has several JSON-related posts already drafted **locally, uncommitted** — finishing work, not
starting from scratch:
- `_posts/2024-05-26-v0.5.0-released.md` — release announcement stub; needs its "New features"
  section filled in, a link to `creek-kafka`'s JSON docs, the tutorial-links paragraph updated to
  point at the now-real tutorials, and the filename date corrected.
- `_posts/2024-05-26-kafka-json-schema-serialisation-format.md` — deep-dive post; "Results" section
  unfinished, filename date needs correcting. Link to the `json-schema-validation-comparison`
  micro-site rather than re-deriving its benchmark data.
- `_posts/2024-01-{08,09,18}-json-schema-evolution-part-{1,2,3}.md` — drafted series, review for
  consistency with the final feature set before publishing.
- `_docs/02-what-is-creek.md` — still says JSON support "is currently being worked on"; update.
  Search the rest of `_docs` for similar stale wording before publishing.
- `creek-kafka` itself has two doc-only issues already tracked/assigned to the maintainer:
  [#927](https://github.com/creek-service/creek-kafka/issues/927),
  [#928](https://github.com/creek-service/creek-kafka/issues/928) — its own docs currently call the
  JSON format "still under development".
- Cross-link the tutorials from the announcement post / tutorials index.
- Commit and push (currently local-only changes in that repo's working tree).

### 10. Minor: decide on `connected-services-demo`

Last touched 2023-03-08, looks superseded by `ks-connected-services-demo`. Confirm with maintainer
whether to archive rather than spend effort adding JSON support to it.

---

## Tips for whoever picks this up

- **Merge conflicts will likely recur.** An unrelated `chore: migrate dependency versions from extra
  properties to gradle.properties` change landed on every repo's `main` mid-cycle and had to be
  reconciled in every PR. If it (or something similar) happens again: keep the JSON-specific
  additions, re-express version lookups via `property("...")` instead of the old `by extra` pattern,
  and add any new property key to `gradle.properties`.
- **A `CONFLICTING` PR gets zero `pull_request`-triggered CI.** If checks silently stop appearing
  after a push, check `mergeStateStatus` via `gh pr view --json mergeable,mergeStateStatus` before
  assuming something else broke.
- **`docs/Gemfile.lock` is gitignored** in every one of these repos — no lockfile management needed
  when changing `docs/Gemfile`.
- **SNAPSHOT dependency sets can go stale mid-session** (they're republished from each repo's `main`
  on every push). If a build fails with something like a `NoSuchMethodError` between two otherwise
  identical runs, suspect environment drift before suspecting your diff — `git stash` the change and
  re-run; if the failure persists on the pristine branch, it's environmental, not yours.
- Local sibling checkouts of every `creek-service` org repo exist side by side on this machine, which
  is how upstream bugs/behaviour get verified directly against source rather than guessed at from
  the outside.
