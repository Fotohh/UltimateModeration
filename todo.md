# Deferred backend work

These are intentionally deferred until the moderation functionality is built and there is a concrete need for them.

- **Player-profile schema migrations:** before changing a released player-profile schema, add an explicit migration path instead of regenerating version-mismatched profiles. Until then, an unsupported profile version must fail startup and leave the original file untouched.
- **Dirty-profile tracking:** if saving every cached profile becomes expensive at scale, track modified profiles and autosave only dirty profiles. The current save-all approach is intentionally simpler for now.
