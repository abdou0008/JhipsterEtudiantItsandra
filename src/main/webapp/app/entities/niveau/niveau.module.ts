import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { NiveauComponent } from './list/niveau.component';
import { NiveauDetailComponent } from './detail/niveau-detail.component';
import { NiveauUpdateComponent } from './update/niveau-update.component';
import { NiveauDeleteDialogComponent } from './delete/niveau-delete-dialog.component';
import { NiveauRoutingModule } from './route/niveau-routing.module';

@NgModule({
  imports: [SharedModule, NiveauRoutingModule],
  declarations: [NiveauComponent, NiveauDetailComponent, NiveauUpdateComponent, NiveauDeleteDialogComponent],
  entryComponents: [NiveauDeleteDialogComponent],
})
export class NiveauModule {}
