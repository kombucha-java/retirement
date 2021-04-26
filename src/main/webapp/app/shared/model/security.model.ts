import { IPurchase } from 'app/shared/model/purchase.model';
import { SecurityType } from 'app/shared/model/enumerations/security-type.model';
import { Region } from 'app/shared/model/enumerations/region.model';

export interface ISecurity {
  id?: number;
  name?: string | null;
  ticker?: string | null;
  type?: SecurityType | null;
  region?: Region | null;
  purchases?: IPurchase[] | null;
}

export const defaultValue: Readonly<ISecurity> = {};
