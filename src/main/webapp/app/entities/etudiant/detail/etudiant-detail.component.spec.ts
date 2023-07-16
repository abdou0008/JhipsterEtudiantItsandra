import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { EtudiantDetailComponent } from './etudiant-detail.component';

describe('Etudiant Management Detail Component', () => {
  let comp: EtudiantDetailComponent;
  let fixture: ComponentFixture<EtudiantDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EtudiantDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ etudiant: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(EtudiantDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(EtudiantDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load etudiant on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.etudiant).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
