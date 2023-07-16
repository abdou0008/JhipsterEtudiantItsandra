import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IFiliere, Filiere } from '../filiere.model';

import { FiliereService } from './filiere.service';

describe('Filiere Service', () => {
  let service: FiliereService;
  let httpMock: HttpTestingController;
  let elemDefault: IFiliere;
  let expectedResult: IFiliere | IFiliere[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(FiliereService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      nomFiliere: 'AAAAAAA',
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

    it('should create a Filiere', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Filiere()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Filiere', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nomFiliere: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Filiere', () => {
      const patchObject = Object.assign(
        {
          nomFiliere: 'BBBBBB',
        },
        new Filiere()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Filiere', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nomFiliere: 'BBBBBB',
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

    it('should delete a Filiere', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addFiliereToCollectionIfMissing', () => {
      it('should add a Filiere to an empty array', () => {
        const filiere: IFiliere = { id: 123 };
        expectedResult = service.addFiliereToCollectionIfMissing([], filiere);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(filiere);
      });

      it('should not add a Filiere to an array that contains it', () => {
        const filiere: IFiliere = { id: 123 };
        const filiereCollection: IFiliere[] = [
          {
            ...filiere,
          },
          { id: 456 },
        ];
        expectedResult = service.addFiliereToCollectionIfMissing(filiereCollection, filiere);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Filiere to an array that doesn't contain it", () => {
        const filiere: IFiliere = { id: 123 };
        const filiereCollection: IFiliere[] = [{ id: 456 }];
        expectedResult = service.addFiliereToCollectionIfMissing(filiereCollection, filiere);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(filiere);
      });

      it('should add only unique Filiere to an array', () => {
        const filiereArray: IFiliere[] = [{ id: 123 }, { id: 456 }, { id: 31910 }];
        const filiereCollection: IFiliere[] = [{ id: 123 }];
        expectedResult = service.addFiliereToCollectionIfMissing(filiereCollection, ...filiereArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const filiere: IFiliere = { id: 123 };
        const filiere2: IFiliere = { id: 456 };
        expectedResult = service.addFiliereToCollectionIfMissing([], filiere, filiere2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(filiere);
        expect(expectedResult).toContain(filiere2);
      });

      it('should accept null and undefined values', () => {
        const filiere: IFiliere = { id: 123 };
        expectedResult = service.addFiliereToCollectionIfMissing([], null, filiere, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(filiere);
      });

      it('should return initial array if no Filiere is added', () => {
        const filiereCollection: IFiliere[] = [{ id: 123 }];
        expectedResult = service.addFiliereToCollectionIfMissing(filiereCollection, undefined, null);
        expect(expectedResult).toEqual(filiereCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
