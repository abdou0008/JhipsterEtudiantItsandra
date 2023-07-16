import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IEtudiant, Etudiant } from '../etudiant.model';
import { EtudiantService } from '../service/etudiant.service';

@Injectable({ providedIn: 'root' })
export class EtudiantRoutingResolveService implements Resolve<IEtudiant> {
  constructor(protected service: EtudiantService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IEtudiant> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((etudiant: HttpResponse<Etudiant>) => {
          if (etudiant.body) {
            return of(etudiant.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Etudiant());
  }
}
