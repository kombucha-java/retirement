import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './purchase.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IPurchaseDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const PurchaseDetail = (props: IPurchaseDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { purchaseEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="purchaseDetailsHeading">
          <Translate contentKey="retirementApp.purchase.detail.title">Purchase</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{purchaseEntity.id}</dd>
          <dt>
            <span id="price">
              <Translate contentKey="retirementApp.purchase.price">Price</Translate>
            </span>
          </dt>
          <dd>{purchaseEntity.price}</dd>
          <dt>
            <span id="currency">
              <Translate contentKey="retirementApp.purchase.currency">Currency</Translate>
            </span>
          </dt>
          <dd>{purchaseEntity.currency}</dd>
          <dt>
            <span id="number">
              <Translate contentKey="retirementApp.purchase.number">Number</Translate>
            </span>
          </dt>
          <dd>{purchaseEntity.number}</dd>
          <dt>
            <span id="comission">
              <Translate contentKey="retirementApp.purchase.comission">Comission</Translate>
            </span>
          </dt>
          <dd>{purchaseEntity.comission}</dd>
          <dt>
            <Translate contentKey="retirementApp.purchase.security">Security</Translate>
          </dt>
          <dd>{purchaseEntity.security ? purchaseEntity.security.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/purchase" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/purchase/${purchaseEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ purchase }: IRootState) => ({
  purchaseEntity: purchase.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(PurchaseDetail);
