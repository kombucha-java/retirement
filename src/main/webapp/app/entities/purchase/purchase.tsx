import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Col, Row, Table } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntities } from './purchase.reducer';
import { IPurchase } from 'app/shared/model/purchase.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IPurchaseProps extends StateProps, DispatchProps, RouteComponentProps<{ url: string }> {}

export const Purchase = (props: IPurchaseProps) => {
  useEffect(() => {
    props.getEntities();
  }, []);

  const handleSyncList = () => {
    props.getEntities();
  };

  const { purchaseList, match, loading } = props;
  return (
    <div>
      <h2 id="purchase-heading" data-cy="PurchaseHeading">
        <Translate contentKey="retirementApp.purchase.home.title">Purchases</Translate>
        <div className="d-flex justify-content-end">
          <Button className="mr-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="retirementApp.purchase.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to={`${match.url}/new`} className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="retirementApp.purchase.home.createLabel">Create new Purchase</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {purchaseList && purchaseList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="retirementApp.purchase.id">ID</Translate>
                </th>
                <th>
                  <Translate contentKey="retirementApp.purchase.price">Price</Translate>
                </th>
                <th>
                  <Translate contentKey="retirementApp.purchase.currency">Currency</Translate>
                </th>
                <th>
                  <Translate contentKey="retirementApp.purchase.number">Number</Translate>
                </th>
                <th>
                  <Translate contentKey="retirementApp.purchase.comission">Comission</Translate>
                </th>
                <th>
                  <Translate contentKey="retirementApp.purchase.security">Security</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {purchaseList.map((purchase, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`${match.url}/${purchase.id}`} color="link" size="sm">
                      {purchase.id}
                    </Button>
                  </td>
                  <td>{purchase.price}</td>
                  <td>
                    <Translate contentKey={`retirementApp.Currency.${purchase.currency}`} />
                  </td>
                  <td>{purchase.number}</td>
                  <td>{purchase.comission}</td>
                  <td>{purchase.security ? <Link to={`security/${purchase.security.id}`}>{purchase.security.id}</Link> : ''}</td>
                  <td className="text-right">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`${match.url}/${purchase.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${purchase.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${purchase.id}/delete`} color="danger" size="sm" data-cy="entityDeleteButton">
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
              <Translate contentKey="retirementApp.purchase.home.notFound">No Purchases found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

const mapStateToProps = ({ purchase }: IRootState) => ({
  purchaseList: purchase.entities,
  loading: purchase.loading,
});

const mapDispatchToProps = {
  getEntities,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(Purchase);
