import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Col, Row, Table } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntities } from './security.reducer';
import { ISecurity } from 'app/shared/model/security.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface ISecurityProps extends StateProps, DispatchProps, RouteComponentProps<{ url: string }> {}

export const Security = (props: ISecurityProps) => {
  useEffect(() => {
    props.getEntities();
  }, []);

  const handleSyncList = () => {
    props.getEntities();
  };

  const { securityList, match, loading } = props;
  return (
    <div>
      <h2 id="security-heading" data-cy="SecurityHeading">
        <Translate contentKey="retirementApp.security.home.title">Securities</Translate>
        <div className="d-flex justify-content-end">
          <Button className="mr-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="retirementApp.security.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to={`${match.url}/new`} className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="retirementApp.security.home.createLabel">Create new Security</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {securityList && securityList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="retirementApp.security.id">ID</Translate>
                </th>
                <th>
                  <Translate contentKey="retirementApp.security.name">Name</Translate>
                </th>
                <th>
                  <Translate contentKey="retirementApp.security.ticker">Ticker</Translate>
                </th>
                <th>
                  <Translate contentKey="retirementApp.security.type">Type</Translate>
                </th>
                <th>
                  <Translate contentKey="retirementApp.security.region">Region</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {securityList.map((security, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`${match.url}/${security.id}`} color="link" size="sm">
                      {security.id}
                    </Button>
                  </td>
                  <td>{security.name}</td>
                  <td>{security.ticker}</td>
                  <td>
                    <Translate contentKey={`retirementApp.SecurityType.${security.type}`} />
                  </td>
                  <td>
                    <Translate contentKey={`retirementApp.Region.${security.region}`} />
                  </td>
                  <td className="text-right">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`${match.url}/${security.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${security.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${security.id}/delete`} color="danger" size="sm" data-cy="entityDeleteButton">
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="retirementApp.security.home.notFound">No Securities found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

const mapStateToProps = ({ security }: IRootState) => ({
  securityList: security.entities,
  loading: security.loading,
});

const mapDispatchToProps = {
  getEntities,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(Security);
