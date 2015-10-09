# Introduction #

This is still work in progress


# Details #

Basically we have the following options of doing backups:

## VM-level: ##
  * Could be implemented by using HFS. This approach backs up the whole operating system rather than just the data. Since the vidaas nodes are created from images and customised with a script with configuration files stored in subversion VM-level backups don't really fit in well with the overall architecture of the system but wouldn't hurt either.

## File-based backup of the database: ##
  * To backup individual files (e.g database dump) could use any online storage or the TSM client to access Oxford's HFS service. To use the HFS we would have to use the TSM client which is incredibly clunky and awkward to integrate with applications (e.g. EIDCSR project). Every node using the TSM client needs login credentials which cannot be automatically obtained as far as I know. We could use shared credentials or proxies but I'd rather not have to.

## Direct Database backup: ##
  * Postgres has already built-in support to be backed-up or synced over the network. It's simple and the backups can be as fast down to transaction level. We can improve on this by offering HA functionality at a later point. It is also more efficient the VM-level or file-based backups (data only sent once, no degraded service during backup schedule,...).