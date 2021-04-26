import axios from 'axios';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IPurchase, defaultValue } from 'app/shared/model/purchase.model';

export const ACTION_TYPES = {
  FETCH_PURCHASE_LIST: 'purchase/FETCH_PURCHASE_LIST',
  FETCH_PURCHASE: 'purchase/FETCH_PURCHASE',
  CREATE_PURCHASE: 'purchase/CREATE_PURCHASE',
  UPDATE_PURCHASE: 'purchase/UPDATE_PURCHASE',
  PARTIAL_UPDATE_PURCHASE: 'purchase/PARTIAL_UPDATE_PURCHASE',
  DELETE_PURCHASE: 'purchase/DELETE_PURCHASE',
  RESET: 'purchase/RESET',
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IPurchase>,
  entity: defaultValue,
  updating: false,
  updateSuccess: false,
};

export type PurchaseState = Readonly<typeof initialState>;

// Reducer

export default (state: PurchaseState = initialState, action): PurchaseState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_PURCHASE_LIST):
    case REQUEST(ACTION_TYPES.FETCH_PURCHASE):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true,
      };
    case REQUEST(ACTION_TYPES.CREATE_PURCHASE):
    case REQUEST(ACTION_TYPES.UPDATE_PURCHASE):
    case REQUEST(ACTION_TYPES.DELETE_PURCHASE):
    case REQUEST(ACTION_TYPES.PARTIAL_UPDATE_PURCHASE):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true,
      };
    case FAILURE(ACTION_TYPES.FETCH_PURCHASE_LIST):
    case FAILURE(ACTION_TYPES.FETCH_PURCHASE):
    case FAILURE(ACTION_TYPES.CREATE_PURCHASE):
    case FAILURE(ACTION_TYPES.UPDATE_PURCHASE):
    case FAILURE(ACTION_TYPES.PARTIAL_UPDATE_PURCHASE):
    case FAILURE(ACTION_TYPES.DELETE_PURCHASE):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload,
      };
    case SUCCESS(ACTION_TYPES.FETCH_PURCHASE_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.FETCH_PURCHASE):
      return {
        ...state,
        loading: false,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.CREATE_PURCHASE):
    case SUCCESS(ACTION_TYPES.UPDATE_PURCHASE):
    case SUCCESS(ACTION_TYPES.PARTIAL_UPDATE_PURCHASE):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.DELETE_PURCHASE):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: {},
      };
    case ACTION_TYPES.RESET:
      return {
        ...initialState,
      };
    default:
      return state;
  }
};

const apiUrl = 'api/purchases';

// Actions

export const getEntities: ICrudGetAllAction<IPurchase> = (page, size, sort) => ({
  type: ACTION_TYPES.FETCH_PURCHASE_LIST,
  payload: axios.get<IPurchase>(`${apiUrl}?cacheBuster=${new Date().getTime()}`),
});

export const getEntity: ICrudGetAction<IPurchase> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_PURCHASE,
    payload: axios.get<IPurchase>(requestUrl),
  };
};

export const createEntity: ICrudPutAction<IPurchase> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_PURCHASE,
    payload: axios.post(apiUrl, cleanEntity(entity)),
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IPurchase> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_PURCHASE,
    payload: axios.put(`${apiUrl}/${entity.id}`, cleanEntity(entity)),
  });
  return result;
};

export const partialUpdate: ICrudPutAction<IPurchase> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.PARTIAL_UPDATE_PURCHASE,
    payload: axios.patch(`${apiUrl}/${entity.id}`, cleanEntity(entity)),
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<IPurchase> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_PURCHASE,
    payload: axios.delete(requestUrl),
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET,
});
