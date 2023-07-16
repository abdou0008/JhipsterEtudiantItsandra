import { entityItemSelector } from '../../support/commands';
import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('Niveau e2e test', () => {
  const niveauPageUrl = '/niveau';
  const niveauPageUrlPattern = new RegExp('/niveau(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const niveauSample = {};

  let niveau: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/niveaus+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/niveaus').as('postEntityRequest');
    cy.intercept('DELETE', '/api/niveaus/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (niveau) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/niveaus/${niveau.id}`,
      }).then(() => {
        niveau = undefined;
      });
    }
  });

  it('Niveaus menu should load Niveaus page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('niveau');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Niveau').should('exist');
    cy.url().should('match', niveauPageUrlPattern);
  });

  describe('Niveau page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(niveauPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Niveau page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/niveau/new$'));
        cy.getEntityCreateUpdateHeading('Niveau');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', niveauPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/niveaus',
          body: niveauSample,
        }).then(({ body }) => {
          niveau = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/niveaus+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/niveaus?page=0&size=20>; rel="last",<http://localhost/api/niveaus?page=0&size=20>; rel="first"',
              },
              body: [niveau],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(niveauPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Niveau page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('niveau');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', niveauPageUrlPattern);
      });

      it('edit button click should load edit Niveau page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Niveau');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', niveauPageUrlPattern);
      });

      it('last delete button click should delete instance of Niveau', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('niveau').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', niveauPageUrlPattern);

        niveau = undefined;
      });
    });
  });

  describe('new Niveau page', () => {
    beforeEach(() => {
      cy.visit(`${niveauPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Niveau');
    });

    it('should create an instance of Niveau', () => {
      cy.get(`[data-cy="nomNiveau"]`).type('proactive Cheese').should('have.value', 'proactive Cheese');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        niveau = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', niveauPageUrlPattern);
    });
  });
});
