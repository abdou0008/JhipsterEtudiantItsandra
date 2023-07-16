import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { INiveau, getNiveauIdentifier } from '../niveau.model';

export type EntityResponseType = HttpResponse<INiveau>;
export type EntityArrayResponseType = HttpResponse<INiveau[]>;

@Injectable({ providedIn: 'root' })
export class NiveauService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/niveaus');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(niveau: INiveau): Observable<EntityResponseType> {
    return this.http.post<INiveau>(this.resourceUrl, niveau, { observe: 'response' });
  }

  update(niveau: INiveau): Observable<EntityResponseType> {
    return this.http.put<INiveau>(`${this.resourceUrl}/${getNiveauIdentifier(niveau) as number}`, niveau, { observe: 'response' });
  }

  partialUpdate(niveau: INiveau): Observable<EntityResponseType> {
    return this.http.patch<INiveau>(`${this.resourceUrl}/${getNiveauIdentifier(niveau) as number}`, niveau, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<INiveau>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<INiveau[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addNiveauToCollectionIfMissing(niveauCollection: INiveau[], ...niveausToCheck: (INiveau | null | undefined)[]): INiveau[] {
    const niveaus: INiveau[] = niveausToCheck.filter(isPresent);
    if (niveaus.length > 0) {
      const niveauCollectionIdentifiers = niveauCollection.map(niveauItem => getNiveauIdentifier(niveauItem)!);
      const niveausToAdd = niveaus.filter(niveauItem => {
        const niveauIdentifier = getNiveauIdentifier(niveauItem);
        if (niveauIdentifier == null || niveauCollectionIdentifiers.includes(niveauIdentifier)) {
          return false;
        }
        niveauCollectionIdentifiers.push(niveauIdentifier);
        return true;
      });
      return [...niveausToAdd, ...niveauCollection];
    }
    return niveauCollection;
  }
}
