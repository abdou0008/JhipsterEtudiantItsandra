import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { INiveau, Niveau } from '../niveau.model';
import { NiveauService } from '../service/niveau.service';

@Injectable({ providedIn: 'root' })
export class NiveauRoutingResolveService implements Resolve<INiveau> {
  constructor(protected service: NiveauService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<INiveau> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((niveau: HttpResponse<Niveau>) => {
          if (niveau.body) {
            return of(niveau.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Niveau());
  }
}
