import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IFiliere } from '../filiere.model';
import { FiliereService } from '../service/filiere.service';

@Component({
  templateUrl: './filiere-delete-dialog.component.html',
})
export class FiliereDeleteDialogComponent {
  filiere?: IFiliere;

  constructor(protected filiereService: FiliereService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.filiereService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
