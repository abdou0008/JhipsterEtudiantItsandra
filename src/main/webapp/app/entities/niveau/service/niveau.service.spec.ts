import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { INiveau, Niveau } from '../niveau.model';

import { NiveauService } from './niveau.service';

describe('Niveau Service', () => {
  let service: NiveauService;
  let httpMock: HttpTestingController;
  let elemDefault: INiveau;
  let expectedResult: INiveau | INiveau[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(NiveauService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      nomNiveau: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Niveau', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Niveau()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Niveau', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nomNiveau: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Niveau', () => {
      const patchObject = Object.assign(
        {
          nomNiveau: 'BBBBBB',
        },
        new Niveau()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Niveau', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nomNiveau: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Niveau', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addNiveauToCollectionIfMissing', () => {
      it('should add a Niveau to an empty array', () => {
        const niveau: INiveau = { id: 123 };
        expectedResult = service.addNiveauToCollectionIfMissing([], niveau);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(niveau);
      });

      it('should not add a Niveau to an array that contains it', () => {
        const niveau: INiveau = { id: 123 };
        const niveauCollection: INiveau[] = [
          {
            ...niveau,
          },
          { id: 456 },
        ];
        expectedResult = service.addNiveauToCollectionIfMissing(niveauCollection, niveau);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Niveau to an array that doesn't contain it", () => {
        const niveau: INiveau = { id: 123 };
        const niveauCollection: INiveau[] = [{ id: 456 }];
        expectedResult = service.addNiveauToCollectionIfMissing(niveauCollection, niveau);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(niveau);
      });

      it('should add only unique Niveau to an array', () => {
        const niveauArray: INiveau[] = [{ id: 123 }, { id: 456 }, { id: 42235 }];
        const niveauCollection: INiveau[] = [{ id: 123 }];
        expectedResult = service.addNiveauToCollectionIfMissing(niveauCollection, ...niveauArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const niveau: INiveau = { id: 123 };
        const niveau2: INiveau = { id: 456 };
        expectedResult = service.addNiveauToCollectionIfMissing([], niveau, niveau2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(niveau);
        expect(expectedResult).toContain(niveau2);
      });

      it('should accept null and undefined values', () => {
        const niveau: INiveau = { id: 123 };
        expectedResult = service.addNiveauToCollectionIfMissing([], null, niveau, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(niveau);
      });

      it('should return initial array if no Niveau is added', () => {
        const niveauCollection: INiveau[] = [{ id: 123 }];
        expectedResult = service.addNiveauToCollectionIfMissing(niveauCollection, undefined, null);
        expect(expectedResult).toEqual(niveauCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
