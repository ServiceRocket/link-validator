package net.customware.confluence.plugin.linkvalidator;

import java.util.Collection;
import java.util.List;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.ListBuilder;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.SpacesQuery;
import com.atlassian.confluence.spaces.actions.SpaceAware;
import com.atlassian.spring.container.ContainerManager;

public class ExternalLinksAction extends ConfluenceActionSupport implements SpaceAware
{
	private static final long serialVersionUID = 1L;
	private Space space;
    private Collection allContent;
    private SpaceManager spaceManager;
    private PageManager pageManager;

    public boolean isSpaceRequired()
    {
        return false;
    }

    public boolean isViewPermissionRequired()
    {
        return true;
    }

    public void setSpace(Space space)
    {
        this.space = space;
    }

    public Space getSpace()
    {
        return space;
    }
    
    public Collection getAllContent()
    {
        if (allContent == null)
        {
            allContent = new java.util.ArrayList();
            
            if (space != null)
                addSpaceContent(space);
            else
            {
            	ListBuilder<Space> listBuilder = getSpaceManager().getSpaces(SpacesQuery.newQuery().build());
                List<Space> spaces = listBuilder.getRange(0, listBuilder.getAvailableSize());
                for(Space space:spaces)
                {
                    addSpaceContent(space);
                }
            }
        }
        return allContent;
    }

    private void addSpaceContent(Space space)
    {
        allContent.addAll(getPageManager().getBlogPosts(space, true));
        allContent.addAll(getPageManager().getPages(space, true));
    }

    private PageManager getPageManager()
    {
        if (pageManager == null)
            pageManager = (PageManager) ContainerManager.getComponent("pageManager");
        return pageManager;
    }

    private SpaceManager getSpaceManager()
    {
        if (spaceManager == null)
            spaceManager = (SpaceManager) ContainerManager.getComponent("spaceManager");
        return spaceManager;
    }

}
