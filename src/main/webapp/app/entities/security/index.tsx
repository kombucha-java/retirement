import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Security from './security';
import SecurityDetail from './security-detail';
import SecurityUpdate from './security-update';
import SecurityDeleteDialog from './security-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={SecurityUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={SecurityUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={SecurityDetail} />
      <ErrorBoundaryRoute path={match.url} component={Security} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={SecurityDeleteDialog} />
  </>
);

export default Routes;
