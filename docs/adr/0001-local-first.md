# ADR 0001: Local-first storage

Status: accepted

Subtrack stores subscription data in a local Room database and does not require an account or backend.

This keeps the MVP private, reliable offline and cheap to maintain. Sync can be added later behind a repository boundary if there is a concrete need.
