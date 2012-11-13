
package it.wm.android.adaptor;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.viewpagerindicator.IconPagerAdapter;

import it.wm.perdue.EsercentiListFragment;

public class EsercentiPagerAdapter extends FragmentPagerAdapter implements IconPagerAdapter {
    
    protected String[] CONTENT  = new String[] {
            "Distanza", "Prezzo", "Nome"
                                };
    
    private int        mCount   = CONTENT.length;
    private String     category = "";
    
    public EsercentiPagerAdapter(FragmentManager fm, String category) {
        super(fm);
        this.category = category;
    }
    
    @Override
    public Fragment getItem(int position) {
        
        // il problema ora  che bisogna gestire con il fragment manager i vari
        // fragment?
        // inoltre bisognerebbe ritornare ad "esercentiBaseActivity" quale
        // fragment  visualizzato,
        // perch quando si fa ad esempio la ricerca o si cambia filtro, bisogna
        // inviare al fragment i dati per le nuove query
        
        return EsercentiListFragment.newInstance(CONTENT[position % CONTENT.length], category);
        
        // return TestFragment.newInstance(CONTENT[position % CONTENT.length]);
    }
    
    @Override
    public int getCount() {
        return mCount;
    }
    
    @Override
    public CharSequence getPageTitle(int position) {
        return CONTENT[position % CONTENT.length];
    }
    
    // public void setCount(int count) {
    // if (count > 0 && count <= 2) {
    // mCount = count;
    // notifyDataSetChanged();
    // }
    // }
    
    @Override
    public int getIconResId(int index) {
        // TODO Auto-generated method stub
        return 0;
    }
}
