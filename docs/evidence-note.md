# AI-resistance / localisation evidence note

Short note explaining how the dataset was obtained or constructed from local
knowledge (Ghana waste/sanitation context) without exposing personal data.
Required by the brief (Section 2).

## Service requests 1–100 — Wiafe Franklin Asare

**What these records are.** Synthetic service-request records built to a
documented rule set. They are not observed municipal data and are not presented
as such. What is genuinely local is the structure they encode: the collection
points are real Greater Accra suburbs, the disposal sites are real transfer
stations and landfills serving the area, each suburb is mapped to the facility
that actually serves it, and the daily rhythm reflects how collection runs —
early-morning crew starts, a late-afternoon second peak, heavier Wednesday and
Saturday market days, a lighter Sunday shift.

**Why the rules matter more than the rows.** Every field is derived from a
stated rule rather than filled in arbitrarily, so any row can be justified on
demand and the whole file can be regenerated identically:

- `destination` is the nearest disposal facility to `source` — a fixed mapping,
  not a random draw, so requests from one suburb always route to one site.
- `category` depends on the kind of location (market, high-density, residential,
  commercial).
- `urgency` depends on public-health risk carried by the category, never on who
  reported it. Medical waste is never below `HIGH`.
- `deadline = timeSubmitted + SLA`, with SLA fixed per urgency
  (CRITICAL 4 h, HIGH 12 h, MEDIUM 48 h, LOW 120 h).
- `status` follows how long the request had been waiting at a single snapshot
  instant, 2026-06-14 18:00.

Full rule set and the resulting distributions: `report/queue-section.md` §6.

**Personal data.** None, by construction. Every column is an integer ID, a term
from a controlled vocabulary, or a timestamp. There are no names, addresses,
phone numbers or free-text fields anywhere in the file — a request is tied to a
collection point, never to a person or a household.

**Still to reconcile.** These rows assume `locationId` 1–45 are collection points
and 46–50 are disposal facilities. The `locations` table is owned by Desmond Kimi
Bilabia and Kelvin Amaah Mankata; if their final IDs differ, only the `source`
and `destination` columns need remapping.
