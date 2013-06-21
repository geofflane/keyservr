package util

/**
 * @author geoff
 * @since 6/20/13
 */
object TestUtil {
  val rawPublicKey = """-----BEGIN PGP PUBLIC KEY BLOCK-----
Version: OpenPGP.js v.1.20130228
Comment: http://openpgpjs.org

xsBNBFGx8n4BCACLcaVvDGeQuyJN7t0DwjA5fDsDXwQTOcsNV3amcF8A7oHn
0kWmVKdeq3IvTK7YHqzmGrtmkOpb7aLw+zkZb+srlbaB1FfYaFbPBVk6XaQY
ByEHVex9pPLj9Moa+2/KRKfbo1rcejbISLykGFKqOg6qPwjM0/pjJ4WxKNni
ISzwh+EIYpyNUux5nAcB1X0peoMq0azubmMawi9o/WYCdv90HyHwXapAGk1l
orVpmbXQdMwd1Cs4xMrPHEKrK6nhRLFG/pYTmNDlEIECemntkfx1hXiw7EXP
zj193SiJEFknK5E4+Jw/4qmHf3ZV8jOfrEyzALcAja1Ht/veKcbQyTcNABEB
AAHNI0dlb2ZmcmV5IE0gTGFuZSA8Z2VvZmZAem9yY2hlZC5uZXQ+wsBcBBAB
AgAQBQJRsfJ/CRAPuxAYW2v3XgAACE0H/3UfXijiTcCEA8HNK/P0ioUz9q/Z
R/HIlnjq2gTG+Q9Dsb1Up7wpQUZn2fjcZjNIAAgXRBc6IiOcF3SLSWGsV2ap
2fK2GxyXAy272IMzLo0f3hbBnRkEXr8WdqPuwOcxV10rDSWdy79MzO8IDg77
4BqJmapdELI0dg1I/T5sYIq9yRPShADdMM9LIAkGcJJpsOnaTf39VdJqeFO5
CxAlrpoYen8PWef+Y8xdTOA/1UGOVeDTD6Yz78o1KhJGAfKvdRxTUXzWV88l
6JlWUD86jE2CYpnhPmJqndq/aaw56a4BmNQiTpUCztXfOgBwde3SsYI2aF+o
k2jMa3uu9qerETw=
=DOgA
-----END PGP PUBLIC KEY BLOCK-----
"""

  val inMemoryDatabase = Map[String, String](
    "db.default.driver" -> "org.h2.Driver",
    "db.default.url"    -> "jdbc:h2:mem:test;MODE=PostgreSQL"
  )
}
