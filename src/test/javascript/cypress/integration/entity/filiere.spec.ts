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

describe('Filiere e2e test', () => {
  const filierePageUrl = '/filiere';
  const filierePageUrlPattern = new RegExp('/filiere(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const filiereSample = {};

  let filiere: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/filieres+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/filieres').as('postEntityRequest');
    cy.intercept('DELETE', '/api/filieres/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (filiere) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/filieres/${filiere.id}`,
      }).then(() => {
        filiere = undefined;
      });
    }
  });

  it('Filieres menu should load Filieres page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('filiere');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Filiere').should('exist');
    cy.url().should('match', filierePageUrlPattern);
  });

  describe('Filiere page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(filierePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Filiere page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/filiere/new$'));
        cy.getEntityCreateUpdateHeading('Filiere');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', filierePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/filieres',
          body: filiereSample,
        }).then(({ body }) => {
          filiere = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/filieres+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/filieres?page=0&size=20>; rel="last",<http://localhost/api/filieres?page=0&size=20>; rel="first"',
              },
              body: [filiere],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(filierePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Filiere page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('filiere');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', filierePageUrlPattern);
      });

      it('edit button click should load edit Filiere page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Filiere');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', filierePageUrlPattern);
      });

      it('last delete button click should delete instance of Filiere', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('filiere').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', filierePageUrlPattern);

        filiere = undefined;
      });
    });
  });

  describe('new Filiere page', () => {
    beforeEach(() => {
      cy.visit(`${filierePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Filiere');
    });

    it('should create an instance of Filiere', () => {
      cy.get(`[data-cy="nomFiliere"]`).type('bypass').should('have.value', 'bypass');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        filiere = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', filierePageUrlPattern);
    });
  });
});
