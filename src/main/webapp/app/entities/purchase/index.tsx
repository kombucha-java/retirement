import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Purchase from './purchase';
import PurchaseDetail from './purchase-detail';
import PurchaseUpdate from './purchase-update';
import PurchaseDeleteDialog from './purchase-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={PurchaseUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={PurchaseUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={PurchaseDetail} />
      <ErrorBoundaryRoute path={match.url} component={Purchase} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={PurchaseDeleteDialog} />
  </>
);

export default Routes;
