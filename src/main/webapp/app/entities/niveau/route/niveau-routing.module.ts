import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { NiveauComponent } from '../list/niveau.component';
import { NiveauDetailComponent } from '../detail/niveau-detail.component';
import { NiveauUpdateComponent } from '../update/niveau-update.component';
import { NiveauRoutingResolveService } from './niveau-routing-resolve.service';

const niveauRoute: Routes = [
  {
    path: '',
    component: NiveauComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: NiveauDetailComponent,
    resolve: {
      niveau: NiveauRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: NiveauUpdateComponent,
    resolve: {
      niveau: NiveauRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: NiveauUpdateComponent,
    resolve: {
      niveau: NiveauRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(niveauRoute)],
  exports: [RouterModule],
})
export class NiveauRoutingModule {}
