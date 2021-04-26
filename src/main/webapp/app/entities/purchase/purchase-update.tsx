import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { Translate, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { ISecurity } from 'app/shared/model/security.model';
import { getEntities as getSecurities } from 'app/entities/security/security.reducer';
import { getEntity, updateEntity, createEntity, reset } from './purchase.reducer';
import { IPurchase } from 'app/shared/model/purchase.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IPurchaseUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const PurchaseUpdate = (props: IPurchaseUpdateProps) => {
  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const { purchaseEntity, securities, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/purchase');
  };

  useEffect(() => {
    if (isNew) {
      props.reset();
    } else {
      props.getEntity(props.match.params.id);
    }

    props.getSecurities();
  }, []);

  useEffect(() => {
    if (props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);

  const saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const entity = {
        ...purchaseEntity,
        ...values,
        security: securities.find(it => it.id.toString() === values.securityId.toString()),
      };

      if (isNew) {
        props.createEntity(entity);
      } else {
        props.updateEntity(entity);
      }
    }
  };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="retirementApp.purchase.home.createOrEditLabel" data-cy="PurchaseCreateUpdateHeading">
            <Translate contentKey="retirementApp.purchase.home.createOrEditLabel">Create or edit a Purchase</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : purchaseEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="purchase-id">
                    <Translate contentKey="global.field.id">ID</Translate>
                  </Label>
                  <AvInput id="purchase-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="priceLabel" for="purchase-price">
                  <Translate contentKey="retirementApp.purchase.price">Price</Translate>
                </Label>
                <AvField id="purchase-price" data-cy="price" type="string" className="form-control" name="price" />
              </AvGroup>
              <AvGroup>
                <Label id="currencyLabel" for="purchase-currency">
                  <Translate contentKey="retirementApp.purchase.currency">Currency</Translate>
                </Label>
                <AvInput
                  id="purchase-currency"
                  data-cy="currency"
                  type="select"
                  className="form-control"
                  name="currency"
                  value={(!isNew && purchaseEntity.currency) || 'USD'}
                >
                  <option value="USD">{translate('retirementApp.Currency.USD')}</option>
                  <option value="RUB">{translate('retirementApp.Currency.RUB')}</option>
                </AvInput>
              </AvGroup>
              <AvGroup>
                <Label id="numberLabel" for="purchase-number">
                  <Translate contentKey="retirementApp.purchase.number">Number</Translate>
                </Label>
                <AvField id="purchase-number" data-cy="number" type="string" className="form-control" name="number" />
              </AvGroup>
              <AvGroup>
                <Label id="comissionLabel" for="purchase-comission">
                  <Translate contentKey="retirementApp.purchase.comission">Comission</Translate>
                </Label>
                <AvField id="purchase-comission" data-cy="comission" type="string" className="form-control" name="comission" />
              </AvGroup>
              <AvGroup>
                <Label for="purchase-security">
                  <Translate contentKey="retirementApp.purchase.security">Security</Translate>
                </Label>
                <AvInput id="purchase-security" data-cy="security" type="select" className="form-control" name="securityId">
                  <option value="" key="0" />
                  {securities
                    ? securities.map(otherEntity => (
                        <option value={otherEntity.id} key={otherEntity.id}>
                          {otherEntity.id}
                        </option>
                      ))
                    : null}
                </AvInput>
              </AvGroup>
              <Button tag={Link} id="cancel-save" to="/purchase" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </AvForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

const mapStateToProps = (storeState: IRootState) => ({
  securities: storeState.security.entities,
  purchaseEntity: storeState.purchase.entity,
  loading: storeState.purchase.loading,
  updating: storeState.purchase.updating,
  updateSuccess: storeState.purchase.updateSuccess,
});

const mapDispatchToProps = {
  getSecurities,
  getEntity,
  updateEntity,
  createEntity,
  reset,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(PurchaseUpdate);
