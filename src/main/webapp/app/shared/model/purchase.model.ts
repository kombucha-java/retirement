import { ISecurity } from 'app/shared/model/security.model';
import { Currency } from 'app/shared/model/enumerations/currency.model';

export interface IPurchase {
  id?: number;
  price?: number | null;
  currency?: Currency | null;
  number?: number | null;
  comission?: number | null;
  security?: ISecurity | null;
}

export const defaultValue: Readonly<IPurchase> = {};
