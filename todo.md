# Deferred backend work

These are intentionally deferred until the moderation functionality is built and there is a concrete need for them.

- **Player-profile schema migrations:** before changing a released player-profile schema, add an explicit migration path instead of regenerating version-mismatched profiles. Until then, an unsupported profile version must fail startup and leave the original file untouched.
- **Dirty-profile tracking:** if saving every cached profile becomes expensive at scale, track modified profiles and autosave only dirty profiles. The current save-all approach is intentionally simpler for now.

### Backend temptations

- **Audit logging:** add an audit log for all player-profile changes, including the user who made the change and a timestamp. This is useful for debugging and accountability, but can be deferred until there is a need for it.
- **Pagination of player-profile queries:** if the number of player profiles grows large, add pagination to the queries to avoid loading all profiles into memory at once. This can be deferred until there is a need for it.
- **Add confirmation prompts for destructive actions:** when deleting or overwriting player profiles, add confirmation prompts to prevent accidental data loss. This can be deferred until there is a need for it.