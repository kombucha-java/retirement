import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { Translate, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { getEntity, updateEntity, createEntity, reset } from './security.reducer';
import { ISecurity } from 'app/shared/model/security.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface ISecurityUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const SecurityUpdate = (props: ISecurityUpdateProps) => {
  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const { securityEntity, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/security');
  };

  useEffect(() => {
    if (isNew) {
      props.reset();
    } else {
      props.getEntity(props.match.params.id);
    }
  }, []);

  useEffect(() => {
    if (props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);

  const saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const entity = {
        ...securityEntity,
        ...values,
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
          <h2 id="retirementApp.security.home.createOrEditLabel" data-cy="SecurityCreateUpdateHeading">
            <Translate contentKey="retirementApp.security.home.createOrEditLabel">Create or edit a Security</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : securityEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="security-id">
                    <Translate contentKey="global.field.id">ID</Translate>
                  </Label>
                  <AvInput id="security-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="nameLabel" for="security-name">
                  <Translate contentKey="retirementApp.security.name">Name</Translate>
                </Label>
                <AvField id="security-name" data-cy="name" type="text" name="name" />
              </AvGroup>
              <AvGroup>
                <Label id="tickerLabel" for="security-ticker">
                  <Translate contentKey="retirementApp.security.ticker">Ticker</Translate>
                </Label>
                <AvField id="security-ticker" data-cy="ticker" type="text" name="ticker" />
              </AvGroup>
              <AvGroup>
                <Label id="typeLabel" for="security-type">
                  <Translate contentKey="retirementApp.security.type">Type</Translate>
                </Label>
                <AvInput
                  id="security-type"
                  data-cy="type"
                  type="select"
                  className="form-control"
                  name="type"
                  value={(!isNew && securityEntity.type) || 'SHARE'}
                >
                  <option value="SHARE">{translate('retirementApp.SecurityType.SHARE')}</option>
                  <option value="BOND">{translate('retirementApp.SecurityType.BOND')}</option>
                  <option value="FUND">{translate('retirementApp.SecurityType.FUND')}</option>
                </AvInput>
              </AvGroup>
              <AvGroup>
                <Label id="regionLabel" for="security-region">
                  <Translate contentKey="retirementApp.security.region">Region</Translate>
                </Label>
                <AvInput
                  id="security-region"
                  data-cy="region"
                  type="select"
                  className="form-control"
                  name="region"
                  value={(!isNew && securityEntity.region) || 'RUS'}
                >
                  <option value="RUS">{translate('retirementApp.Region.RUS')}</option>
                  <option value="USA">{translate('retirementApp.Region.USA')}</option>
                  <option value="OTHERS">{translate('retirementApp.Region.OTHERS')}</option>
                </AvInput>
              </AvGroup>
              <Button tag={Link} id="cancel-save" to="/security" replace color="info">
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
  securityEntity: storeState.security.entity,
  loading: storeState.security.loading,
  updating: storeState.security.updating,
  updateSuccess: storeState.security.updateSuccess,
});

const mapDispatchToProps = {
  getEntity,
  updateEntity,
  createEntity,
  reset,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(SecurityUpdate);
