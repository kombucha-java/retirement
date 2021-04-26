import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './security.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface ISecurityDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const SecurityDetail = (props: ISecurityDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { securityEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="securityDetailsHeading">
          <Translate contentKey="retirementApp.security.detail.title">Security</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{securityEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="retirementApp.security.name">Name</Translate>
            </span>
          </dt>
          <dd>{securityEntity.name}</dd>
          <dt>
            <span id="ticker">
              <Translate contentKey="retirementApp.security.ticker">Ticker</Translate>
            </span>
          </dt>
          <dd>{securityEntity.ticker}</dd>
          <dt>
            <span id="type">
              <Translate contentKey="retirementApp.security.type">Type</Translate>
            </span>
          </dt>
          <dd>{securityEntity.type}</dd>
          <dt>
            <span id="region">
              <Translate contentKey="retirementApp.security.region">Region</Translate>
            </span>
          </dt>
          <dd>{securityEntity.region}</dd>
        </dl>
        <Button tag={Link} to="/security" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/security/${securityEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ security }: IRootState) => ({
  securityEntity: security.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(SecurityDetail);
