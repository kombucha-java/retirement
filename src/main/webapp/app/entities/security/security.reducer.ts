import axios from 'axios';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { ISecurity, defaultValue } from 'app/shared/model/security.model';

export const ACTION_TYPES = {
  FETCH_SECURITY_LIST: 'security/FETCH_SECURITY_LIST',
  FETCH_SECURITY: 'security/FETCH_SECURITY',
  CREATE_SECURITY: 'security/CREATE_SECURITY',
  UPDATE_SECURITY: 'security/UPDATE_SECURITY',
  PARTIAL_UPDATE_SECURITY: 'security/PARTIAL_UPDATE_SECURITY',
  DELETE_SECURITY: 'security/DELETE_SECURITY',
  RESET: 'security/RESET',
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<ISecurity>,
  entity: defaultValue,
  updating: false,
  updateSuccess: false,
};

export type SecurityState = Readonly<typeof initialState>;

// Reducer

export default (state: SecurityState = initialState, action): SecurityState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_SECURITY_LIST):
    case REQUEST(ACTION_TYPES.FETCH_SECURITY):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true,
      };
    case REQUEST(ACTION_TYPES.CREATE_SECURITY):
    case REQUEST(ACTION_TYPES.UPDATE_SECURITY):
    case REQUEST(ACTION_TYPES.DELETE_SECURITY):
    case REQUEST(ACTION_TYPES.PARTIAL_UPDATE_SECURITY):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true,
      };
    case FAILURE(ACTION_TYPES.FETCH_SECURITY_LIST):
    case FAILURE(ACTION_TYPES.FETCH_SECURITY):
    case FAILURE(ACTION_TYPES.CREATE_SECURITY):
    case FAILURE(ACTION_TYPES.UPDATE_SECURITY):
    case FAILURE(ACTION_TYPES.PARTIAL_UPDATE_SECURITY):
    case FAILURE(ACTION_TYPES.DELETE_SECURITY):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload,
      };
    case SUCCESS(ACTION_TYPES.FETCH_SECURITY_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.FETCH_SECURITY):
      return {
        ...state,
        loading: false,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.CREATE_SECURITY):
    case SUCCESS(ACTION_TYPES.UPDATE_SECURITY):
    case SUCCESS(ACTION_TYPES.PARTIAL_UPDATE_SECURITY):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.DELETE_SECURITY):
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

const apiUrl = 'api/securities';

// Actions

export const getEntities: ICrudGetAllAction<ISecurity> = (page, size, sort) => ({
  type: ACTION_TYPES.FETCH_SECURITY_LIST,
  payload: axios.get<ISecurity>(`${apiUrl}?cacheBuster=${new Date().getTime()}`),
});

export const getEntity: ICrudGetAction<ISecurity> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_SECURITY,
    payload: axios.get<ISecurity>(requestUrl),
  };
};

export const createEntity: ICrudPutAction<ISecurity> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_SECURITY,
    payload: axios.post(apiUrl, cleanEntity(entity)),
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<ISecurity> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_SECURITY,
    payload: axios.put(`${apiUrl}/${entity.id}`, cleanEntity(entity)),
  });
  return result;
};

export const partialUpdate: ICrudPutAction<ISecurity> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.PARTIAL_UPDATE_SECURITY,
    payload: axios.patch(`${apiUrl}/${entity.id}`, cleanEntity(entity)),
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<ISecurity> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_SECURITY,
    payload: axios.delete(requestUrl),
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET,
});
