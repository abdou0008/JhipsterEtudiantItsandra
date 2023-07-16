import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IFiliere, getFiliereIdentifier } from '../filiere.model';

export type EntityResponseType = HttpResponse<IFiliere>;
export type EntityArrayResponseType = HttpResponse<IFiliere[]>;

@Injectable({ providedIn: 'root' })
export class FiliereService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/filieres');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(filiere: IFiliere): Observable<EntityResponseType> {
    return this.http.post<IFiliere>(this.resourceUrl, filiere, { observe: 'response' });
  }

  update(filiere: IFiliere): Observable<EntityResponseType> {
    return this.http.put<IFiliere>(`${this.resourceUrl}/${getFiliereIdentifier(filiere) as number}`, filiere, { observe: 'response' });
  }

  partialUpdate(filiere: IFiliere): Observable<EntityResponseType> {
    return this.http.patch<IFiliere>(`${this.resourceUrl}/${getFiliereIdentifier(filiere) as number}`, filiere, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IFiliere>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IFiliere[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addFiliereToCollectionIfMissing(filiereCollection: IFiliere[], ...filieresToCheck: (IFiliere | null | undefined)[]): IFiliere[] {
    const filieres: IFiliere[] = filieresToCheck.filter(isPresent);
    if (filieres.length > 0) {
      const filiereCollectionIdentifiers = filiereCollection.map(filiereItem => getFiliereIdentifier(filiereItem)!);
      const filieresToAdd = filieres.filter(filiereItem => {
        const filiereIdentifier = getFiliereIdentifier(filiereItem);
        if (filiereIdentifier == null || filiereCollectionIdentifiers.includes(filiereIdentifier)) {
          return false;
        }
        filiereCollectionIdentifiers.push(filiereIdentifier);
        return true;
      });
      return [...filieresToAdd, ...filiereCollection];
    }
    return filiereCollection;
  }
}
