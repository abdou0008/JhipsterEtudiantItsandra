import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { FiliereComponent } from '../list/filiere.component';
import { FiliereDetailComponent } from '../detail/filiere-detail.component';
import { FiliereUpdateComponent } from '../update/filiere-update.component';
import { FiliereRoutingResolveService } from './filiere-routing-resolve.service';

const filiereRoute: Routes = [
  {
    path: '',
    component: FiliereComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: FiliereDetailComponent,
    resolve: {
      filiere: FiliereRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: FiliereUpdateComponent,
    resolve: {
      filiere: FiliereRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: FiliereUpdateComponent,
    resolve: {
      filiere: FiliereRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(filiereRoute)],
  exports: [RouterModule],
})
export class FiliereRoutingModule {}
