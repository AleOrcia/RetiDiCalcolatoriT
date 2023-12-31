/*
 * Please do not edit this file.
 * It was generated using rpcgen.
 */

#include "FattoreX.h"

bool_t
xdr_Input (XDR *xdrs, Input *objp)
{
	register int32_t *buf;

	int i;
	 if (!xdr_vector (xdrs, (char *)objp->candidato, DIM,
		sizeof (char), (xdrproc_t) xdr_char))
		 return FALSE;
	 if (!xdr_vector (xdrs, (char *)objp->giuduce, DIM,
		sizeof (char), (xdrproc_t) xdr_char))
		 return FALSE;
	 if (!xdr_char (xdrs, &objp->categoria))
		 return FALSE;
	 if (!xdr_vector (xdrs, (char *)objp->nomeFile, DIM,
		sizeof (char), (xdrproc_t) xdr_char))
		 return FALSE;
	 if (!xdr_char (xdrs, &objp->fase))
		 return FALSE;
	 if (!xdr_int (xdrs, &objp->voto))
		 return FALSE;
	return TRUE;
}

bool_t
xdr_Tabellone (XDR *xdrs, Tabellone *objp)
{
	register int32_t *buf;

	int i;
	 if (!xdr_vector (xdrs, (char *)objp->riga, NUMFILE,
		sizeof (Input), (xdrproc_t) xdr_Input))
		 return FALSE;
	return TRUE;
}

bool_t
xdr_Voto (XDR *xdrs, Voto *objp)
{
	register int32_t *buf;

	int i;
	 if (!xdr_vector (xdrs, (char *)objp->candidato, DIM,
		sizeof (char), (xdrproc_t) xdr_char))
		 return FALSE;
	 if (!xdr_char (xdrs, &objp->voto))
		 return FALSE;
	return TRUE;
}

bool_t
xdr_Giudice (XDR *xdrs, Giudice *objp)
{
	register int32_t *buf;

	int i;
	 if (!xdr_vector (xdrs, (char *)objp->giudice, DIM,
		sizeof (char), (xdrproc_t) xdr_char))
		 return FALSE;
	 if (!xdr_int (xdrs, &objp->totaleVoti))
		 return FALSE;
	return TRUE;
}

bool_t
xdr_Output (XDR *xdrs, Output *objp)
{
	register int32_t *buf;

	int i;
	 if (!xdr_vector (xdrs, (char *)objp->giudici, NUMFILE,
		sizeof (Giudice), (xdrproc_t) xdr_Giudice))
		 return FALSE;
	return TRUE;
}
